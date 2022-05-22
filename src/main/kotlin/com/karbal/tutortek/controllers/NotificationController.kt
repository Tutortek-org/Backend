package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.notificationDTO.NotificationGetDTO
import com.karbal.tutortek.dto.notificationDTO.EndpointPostDTO
import com.karbal.tutortek.dto.notificationDTO.NotificationPostDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.utils.SNSUtils
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("notifications")
class NotificationController(
    private val userProfileService: UserProfileService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createNotificationEndpoint(@RequestBody endpointPostDTO: EndpointPostDTO, request: HttpServletRequest): NotificationGetDTO {
        verifyEndpointDto(endpointPostDTO)
        val endpointArn = SNSUtils.createEndpoint(endpointPostDTO.deviceToken)

        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val profileId = claims?.get("pid").toString().toLong()
        val profile = userProfileService.getUserProfile(profileId)

        if(!profile.isEmpty) {
            val profileFromDatabase = profile.get()
            if (endpointArn != null) profileFromDatabase.deviceEndpointArn = endpointArn
            userProfileService.saveUserProfile(profileFromDatabase)
        }

        return NotificationGetDTO(endpointArn)
    }

    @PostMapping("send")
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun sendNotification(@RequestBody notificationPostDTO: NotificationPostDTO) {
        verifyNotificationPostDto(notificationPostDTO)
        SNSUtils.sendNotifications(notificationPostDTO)
    }

    private fun verifyEndpointDto(endpointPostDTO: EndpointPostDTO) {
        if(endpointPostDTO.deviceToken.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.EMPTY_DEVICE_TOKEN)
    }

    private fun verifyNotificationPostDto(notificationPostDTO: NotificationPostDTO) {
        if(notificationPostDTO.title.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BLANK_NOTIFICATION_TITLE)

        if(notificationPostDTO.content.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BLANK_NOTIFICATION_CONTENT)
    }
}
