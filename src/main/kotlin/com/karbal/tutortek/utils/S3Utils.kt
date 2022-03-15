package com.karbal.tutortek.utils

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.karbal.tutortek.constants.ApiErrorSlug
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.regions.Region
import java.io.InputStream

@Component
class S3Utils {

    @Value("\${s3.bucket}")
    fun setBucket(bucket: String) {
        BUCKET = bucket
    }

    @Value("\${AWS_ACCESS_KEY_ID}")
    fun setAwsAccessKeyId(keyId: String) {
        AWS_ACCESS_KEY_ID = keyId
    }

    @Value("\${AWS_SECRET_ACCESS_KEY}")
    fun setAwsSecretAccessKey(secretAccessKey: String) {
        AWS_SECRET_ACCESS_KEY = secretAccessKey
    }

    companion object {
        private var BUCKET = ""
        private var AWS_ACCESS_KEY_ID = ""
        private var AWS_SECRET_ACCESS_KEY = ""

        fun uploadFile(fileName: String, inputStream: InputStream) {
            val request = PutObjectRequest(BUCKET, fileName, inputStream, ObjectMetadata())
            getS3Client()?.putObject(request)
        }

        fun downloadFile(objectKey: String): InputStream? {
            try {
                val request = GetObjectRequest(BUCKET, objectKey)
                val result = getS3Client()?.getObject(request)
                return result?.objectContent?.delegateStream
            }
            catch (e: AmazonS3Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NO_PHOTO_EXISTS)
            }
        }

        private fun getS3Client(): AmazonS3? {
            val builder = AmazonS3ClientBuilder.standard()
            builder.region = Region.EU_CENTRAL_1.toString()
            builder.credentials = AWSStaticCredentialsProvider(BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY))
            return builder.build()
        }
    }
}
