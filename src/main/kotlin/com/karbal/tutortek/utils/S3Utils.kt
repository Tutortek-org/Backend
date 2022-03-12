package com.karbal.tutortek.utils

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
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
            val builder = AmazonS3ClientBuilder.standard()
            builder.region = Region.EU_CENTRAL_1.toString()
            builder.credentials = AWSStaticCredentialsProvider(BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY))
            val client = builder.build()
            val request = PutObjectRequest(BUCKET, fileName, inputStream, ObjectMetadata())
            request.cannedAcl = CannedAccessControlList.PublicReadWrite
            client.putObject(request)
        }
    }
}
