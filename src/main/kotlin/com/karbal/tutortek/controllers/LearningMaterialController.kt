package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialGetDTO
import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialPostDTO
import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.utils.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("topics/{topicId}/meetings/{meetingId}/materials")
class LearningMaterialController(
    val learningMaterialService: LearningMaterialService,
    val topicService: TopicService
) {

    @GetMapping
    fun getAllLearningMaterials(@PathVariable topicId: Long, @PathVariable meetingId: Long): List<LearningMaterialGetDTO> {
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        return meeting.learningMaterials.map { lm -> LearningMaterialGetDTO(lm) }
    }

    @GetMapping("/{materialId}")
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addLearningMaterial(@PathVariable topicId: Long,
                            @PathVariable meetingId: Long,
                            @RequestBody learningMaterialDTO: LearningMaterialPostDTO): LearningMaterialGetDTO {
        verifyDto(learningMaterialDTO)
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = LearningMaterial(learningMaterialDTO)
        learningMaterial.meeting = meeting
        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(learningMaterial))
    }

    @DeleteMapping("/{materialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteLearningMaterial(@PathVariable topicId: Long, @PathVariable meetingId: Long, @PathVariable materialId: Long) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        learningMaterial.id?.let { learningMaterialService.deleteLearningMaterial(it) }
    }

    @PutMapping("/{materialId}")
    fun updateLearningMaterial(@PathVariable topicId: Long,
                               @PathVariable meetingId: Long,
                               @PathVariable materialId: Long,
                               @RequestBody learningMaterialDTO: LearningMaterialPostDTO): LearningMaterialGetDTO {
        verifyDto(learningMaterialDTO)
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MATERIAL_NOT_FOUND)

        val learningMaterialFromDto = LearningMaterial(learningMaterialDTO)
        learningMaterialFromDto.id = materialId
        learningMaterialFromDto.meeting = meeting
        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(learningMaterialFromDto))
    }

    fun verifyDto(learningMaterialDTO: LearningMaterialPostDTO) {
        if(learningMaterialDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(learningMaterialDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)

        if(learningMaterialDTO.link.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LINK_EMPTY)
    }
}
