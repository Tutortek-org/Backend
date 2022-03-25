package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.topicDTO.TopicGetDTO
import com.karbal.tutortek.dto.topicDTO.TopicPostDTO
import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("topics")
class TopicController(
    private val topicService: TopicService,
    private val userProfileService: UserProfileService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun addTopic(@RequestBody topicDTO: TopicPostDTO, request: HttpServletRequest): TopicGetDTO {
        verifyDto(topicDTO)
        val topic = convertDtoToEntity(topicDTO, request)
        return TopicGetDTO(topicService.saveTopic(topic))
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun deleteTopic(@PathVariable id: Long){
        val topic = topicService.getTopic(id)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        topicService.deleteTopic(id)
    }

    @GetMapping
    fun getAllTopics() = topicService.getAllTopics().map { t -> TopicGetDTO(t) }

    @GetMapping("personal")
    @RolesAllowed(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun getPersonalTopics(request: HttpServletRequest): List<TopicGetDTO> {
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val userProfileId = claims?.get("pid").toString().toLong()

        val userProfile = userProfileService.getUserProfile(userProfileId)
        if(userProfile.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userFromDatabase = userProfile.get()

        return userFromDatabase.topics.map { t -> TopicGetDTO(t) }
    }

    @GetMapping("unapproved")
    @Secured(Role.ADMIN_ANNOTATION)
    fun getAllUnapproved() = topicService.getAllUnapproved().map { t -> TopicGetDTO(t) }

    @GetMapping("{id}")
    fun getTopic(@PathVariable id: Long): TopicGetDTO {
        val topic = topicService.getTopic(id)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        return TopicGetDTO(topic.get())
    }

    @PutMapping("{id}")
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun updateTopic(@PathVariable id: Long, @RequestBody topicDTO: TopicPostDTO, request: HttpServletRequest): TopicGetDTO {
        verifyDto(topicDTO)
        val topic = convertDtoToEntity(topicDTO, request)
        val topicInDatabase = topicService.getTopic(id)

        if(topicInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val extractedTopic = topicInDatabase.get()
        extractedTopic.copy(topic)
        return TopicGetDTO(topicService.saveTopic(extractedTopic))
    }

    @PutMapping("{id}/approve")
    @Secured(Role.ADMIN_ANNOTATION)
    fun approveTopic(@PathVariable id: Long): TopicGetDTO {
        val topicInDatabase = topicService.getTopic(id)
        if(topicInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val extractedTopic = topicInDatabase.get()
        extractedTopic.isApproved = true
        return TopicGetDTO(topicService.saveTopic(extractedTopic))
    }

    private fun convertDtoToEntity(topicDTO: TopicPostDTO, request: HttpServletRequest): Topic {
        val topic = Topic()
        topic.name = topicDTO.name
        topic.description = topicDTO.description

        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)

        val profileId = claims?.get("pid").toString().toLong()
        val user = userProfileService.getUserProfile(profileId)

        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        topic.userProfile = user.get()
        return topic
    }

    private fun verifyDto(topicDTO: TopicPostDTO) {
        if(topicDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(topicDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)
    }
}
