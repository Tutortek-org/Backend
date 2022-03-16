package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.notificationDTO.NotificationGetDTO
import com.karbal.tutortek.dto.notificationDTO.NotificationPostDTO
import com.karbal.tutortek.utils.SNSUtils
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("notifications")
class NotificationController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotificationEndpoint(@RequestBody notificationPostDTO: NotificationPostDTO): NotificationGetDTO {
        verifyDto(notificationPostDTO)
        val endpointArn = SNSUtils.createEndpoint(notificationPostDTO.deviceToken)
        return NotificationGetDTO(endpointArn)
    }

    private fun verifyDto(notificationPostDTO: NotificationPostDTO) {
        if(notificationPostDTO.deviceToken.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.EMPTY_DEVICE_TOKEN)
    }
}
