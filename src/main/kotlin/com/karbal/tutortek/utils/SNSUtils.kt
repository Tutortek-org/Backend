package com.karbal.tutortek.utils

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSClientBuilder
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult
import com.amazonaws.services.sns.model.Endpoint
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest
import com.amazonaws.services.sns.model.NotFoundException
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest
import com.karbal.tutortek.dto.notificationDTO.NotificationPostDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region

@Component
class SNSUtils {

    @Value("\${AWS_ARN}")
    fun setBucket(arn: String) {
        ARN = arn
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
        private var ARN = ""
        private var AWS_ACCESS_KEY_ID = ""
        private var AWS_SECRET_ACCESS_KEY = ""

        fun sendNotifications(notificationPostDTO: NotificationPostDTO) {
            val client = getSNSClient()
            val endpoints = getEndpointList(client)
            endpoints.forEach {
                val isEnabled = it.attributes["Enabled"].toBoolean()
                if(isEnabled) {
                    val request = PublishRequest()
                        .withTargetArn(it.endpointArn)
                        .withSubject(notificationPostDTO.title)
                        .withMessage(notificationPostDTO.content)
                    client?.publish(request)
                }
            }
        }

        fun createEndpoint(token: String): String? {
            val client = getSNSClient()
            var result = createEndpointInner(token, client)
            result = updateEndpoint(result, token, client)
            return result?.endpointArn
        }

        private fun getEndpointList(client: AmazonSNS?): MutableList<Endpoint> {
            val endpoints = mutableListOf<Endpoint>()
            var nextToken = ""
            do {
                val request = ListEndpointsByPlatformApplicationRequest()
                    .withPlatformApplicationArn(ARN)
                if(nextToken.isNotEmpty()) request.nextToken = nextToken
                val result = client?.listEndpointsByPlatformApplication(request)
                result?.endpoints?.let { endpoints.addAll(it) }
                nextToken = result?.nextToken.toString()
            } while (nextToken.isNotEmpty() && nextToken != "null")
            return endpoints
        }

        private fun createEndpointInner(token: String, client: AmazonSNS?): CreatePlatformEndpointResult? {
            val createPlatformEndpointRequest = CreatePlatformEndpointRequest()
                .withPlatformApplicationArn(ARN)
                .withToken(token)
            return client?.createPlatformEndpoint(createPlatformEndpointRequest)
        }

        private fun updateEndpoint(result: CreatePlatformEndpointResult?, token: String, client: AmazonSNS?): CreatePlatformEndpointResult? {
            var isUpdateNeeded = false
            var toReturn = result
            try {
                val getPlatformEndpointRequest = GetEndpointAttributesRequest().withEndpointArn(result?.endpointArn)
                val getResult = client?.getEndpointAttributes(getPlatformEndpointRequest)
                isUpdateNeeded = !getResult?.attributes?.get("Token").equals(token)
                        || !getResult?.attributes?.get("Enabled")?.lowercase().equals("true")
            }
            catch (e: NotFoundException) {
                toReturn = createEndpointInner(token, client)
            }

            if(isUpdateNeeded) {
                val attributes = hashMapOf<String, String>()
                attributes["Token"] = token
                attributes["Enabled"] = "true"
                val setPlatformEndpointRequest = SetEndpointAttributesRequest()
                    .withEndpointArn(result?.endpointArn)
                    .withAttributes(attributes)
                client?.setEndpointAttributes(setPlatformEndpointRequest)
            }

            return toReturn
        }

        private fun getSNSClient(): AmazonSNS? {
            val builder = AmazonSNSClientBuilder.standard()
            builder.region = Region.EU_CENTRAL_1.toString()
            builder.credentials = AWSStaticCredentialsProvider(BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY))
            return builder.build()
        }
    }
}
