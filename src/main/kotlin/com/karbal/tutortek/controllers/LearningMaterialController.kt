package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialGetDTO
import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialPostDTO
import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.notificationDTO.NotificationPostDTO
import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.utils.SNSUtils
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.annotation.security.RolesAllowed
import javax.servlet.http.HttpServletRequest

@RestController
class LearningMaterialController(
    private val learningMaterialService: LearningMaterialService,
    private val topicService: TopicService,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @GetMapping("topics/{topicId}/meetings/{meetingId}/materials")
    fun getAllLearningMaterials(@PathVariable topicId: Long, @PathVariable meetingId: Long): List<LearningMaterialGetDTO> {
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        return meeting.learningMaterials.filter { lm -> lm.isApproved }.map { lm -> LearningMaterialGetDTO(lm) }
    }

    @GetMapping("topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    fun getLearningMaterial(@PathVariable topicId: Long, @PathVariable meetingId: Long, @PathVariable materialId: Long): LearningMaterialGetDTO {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        return LearningMaterialGetDTO(learningMaterial)
    }

    @GetMapping("materials/unapproved")
    @Secured(Role.ADMIN_ANNOTATION)
    fun getAllUnapproved() = learningMaterialService.getAllUnapproved().map { lm -> LearningMaterialGetDTO(lm) }

    @PostMapping("topics/{topicId}/meetings/{meetingId}/materials")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun addLearningMaterial(@PathVariable topicId: Long,
                            @PathVariable meetingId: Long,
                            @RequestBody learningMaterialDTO: LearningMaterialPostDTO,
                            request: HttpServletRequest): LearningMaterialGetDTO {
        verifyDto(learningMaterialDTO)
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = topicFromDatabase.meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = LearningMaterial(learningMaterialDTO)
        learningMaterial.meeting = meeting
        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(learningMaterial))
    }

    @DeleteMapping("topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun deleteLearningMaterial(@PathVariable topicId: Long,
                               @PathVariable meetingId: Long,
                               @PathVariable materialId: Long,
                               request: HttpServletRequest) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = topicFromDatabase.meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        learningMaterial.id?.let { learningMaterialService.deleteLearningMaterial(it) }
    }

    @PutMapping("topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun updateLearningMaterial(@PathVariable topicId: Long,
                               @PathVariable meetingId: Long,
                               @PathVariable materialId: Long,
                               @RequestBody learningMaterialDTO: LearningMaterialPostDTO,
                               request: HttpServletRequest): LearningMaterialGetDTO {
        verifyDto(learningMaterialDTO)
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = topicFromDatabase.meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        val learningMaterialFromDto = LearningMaterial(learningMaterialDTO)
        learningMaterialFromDto.id = materialId
        learningMaterialFromDto.meeting = meeting
        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(learningMaterialFromDto))
    }

    @PutMapping("materials/{id}/approve")
    @Secured(Role.ADMIN_ANNOTATION)
    fun approveLearningMaterial(@PathVariable id: Long): LearningMaterialGetDTO {
        val material = learningMaterialService.getLearningMaterial(id)
        if(material.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        val extractedMaterial = material.get()
        extractedMaterial.isApproved = true

        val notificationPostDTO = NotificationPostDTO(
            "Learning material approval",
            "Your learning material \"${extractedMaterial.name}\" has been approved"
        )
        SNSUtils.sendNotificationToSingleDevice(extractedMaterial.meeting.topic.userProfile.deviceEndpointArn, notificationPostDTO)

        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(extractedMaterial))
    }

    @DeleteMapping("materials/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(Role.ADMIN_ANNOTATION)
    fun adminReject(@PathVariable id: Long) {
        val material = learningMaterialService.getLearningMaterial(id)
        if(material.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)
        learningMaterialService.deleteLearningMaterial(id)
    }

    private fun verifyUserProfileID(topic: Topic, request: HttpServletRequest) {
        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val profileId = claims?.get("pid").toString().toLong()
        if(topic.userProfile.id != profileId)
            throw ResponseStatusException(HttpStatus.FORBIDDEN, ApiErrorSlug.USER_FORBIDDEN_FROM_ALTERING)
    }

    private fun verifyDto(learningMaterialDTO: LearningMaterialPostDTO) {
        if(learningMaterialDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(learningMaterialDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)

        if(learningMaterialDTO.link.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LINK_EMPTY)
    }
}
