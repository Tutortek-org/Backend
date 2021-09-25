package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialGetDTO
import com.karbal.tutortek.dto.learningMaterialDTO.LearningMaterialPostDTO
import com.karbal.tutortek.entities.LearningMaterial
import com.karbal.tutortek.services.LearningMaterialService
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class LearningMaterialController(val learningMaterialService: LearningMaterialService,
                                 val meetingService: MeetingService,
                                 val topicService: TopicService) {

    @GetMapping("/topics/{topicId}/meetings/{meetingId}/materials")
    fun getAllLearningMaterials(@PathVariable topicId: Long, @PathVariable meetingId: Long): List<LearningMaterialGetDTO> {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        return meeting.learningMaterials.map { lm -> LearningMaterialGetDTO(lm) }
    }

    @GetMapping("/topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    fun getLearningMaterial(@PathVariable topicId: Long, @PathVariable meetingId: Long, @PathVariable materialId: Long): LearningMaterialGetDTO {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        return LearningMaterialGetDTO(learningMaterial)
    }

    @PostMapping("/topics/{topicId}/meetings/{meetingId}/materials")
    fun addLearningMaterial(@PathVariable topicId: Long,
                            @PathVariable meetingId: Long,
                            @RequestBody learningMaterialDTO: LearningMaterialPostDTO): LearningMaterialGetDTO {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val learningMaterial = convertDtoToEntity(learningMaterialDTO)
        learningMaterial.meeting = meeting
        return LearningMaterialGetDTO(learningMaterialService.saveLearningMaterial(learningMaterial))
    }

    @DeleteMapping("/topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    fun deleteLearningMaterial(@PathVariable topicId: Long, @PathVariable meetingId: Long, @PathVariable materialId: Long) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        learningMaterial.id?.let { learningMaterialService.deleteLearningMaterial(it) }
    }

    @PutMapping("/topics/{topicId}/meetings/{meetingId}/materials/{materialId}")
    fun updateLearningMaterial(@PathVariable topicId: Long,
                               @PathVariable meetingId: Long,
                               @PathVariable materialId: Long,
                               @RequestBody learningMaterialDTO: LearningMaterialPostDTO) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val learningMaterial = meeting.learningMaterials.find { lm -> lm.id == materialId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Learning material not found")
        val learningMaterialFromDto = convertDtoToEntity(learningMaterialDTO)
        learningMaterialFromDto.id = materialId
        learningMaterialFromDto.meeting = meeting
        learningMaterialService.saveLearningMaterial(learningMaterialFromDto)
    }

    fun convertDtoToEntity(learningMaterialDTO: LearningMaterialPostDTO): LearningMaterial {
        val learningMaterial = LearningMaterial()
        learningMaterial.name = learningMaterialDTO.name
        learningMaterial.description = learningMaterialDTO.description
        learningMaterial.link = learningMaterialDTO.link
        return learningMaterial
    }
}
