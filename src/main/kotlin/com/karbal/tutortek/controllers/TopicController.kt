package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.topicDTO.TopicGetDTO
import com.karbal.tutortek.dto.topicDTO.TopicPostDTO
import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("topics")
class TopicController(
    val topicService: TopicService,
    val userService: UserService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addTopic(@RequestBody topicDTO: TopicPostDTO): TopicGetDTO {
        verifyDto(topicDTO)
        val topic = convertDtoToEntity(topicDTO)
        return TopicGetDTO(topicService.saveTopic(topic))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTopic(@PathVariable id: Long){
        val topic = topicService.getTopic(id)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        topicService.deleteTopic(id)
    }

    @GetMapping
    fun getAllTopics() = topicService.getAllTopics().map { t -> TopicGetDTO(t) }

    @GetMapping("/{id}")
    fun getTopic(@PathVariable id: Long): TopicGetDTO {
        val topic = topicService.getTopic(id)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        return TopicGetDTO(topic.get())
    }

    @PutMapping("/{id}")
    fun updateTopic(@PathVariable id: Long, @RequestBody topicDTO: TopicPostDTO): TopicGetDTO {
        verifyDto(topicDTO)
        val topic = convertDtoToEntity(topicDTO)
        val topicInDatabase = topicService.getTopic(id)

        if(topicInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val extractedTopic = topicInDatabase.get()
        extractedTopic.copy(topic)
        return TopicGetDTO(topicService.saveTopic(extractedTopic))
    }

    fun convertDtoToEntity(topicDTO: TopicPostDTO): Topic {
        val topic = Topic()
        topic.name = topicDTO.name
        val user = userService.getUser(topicDTO.userId)

        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        topic.user = user.get()
        return topic
    }

    fun verifyDto(topicDTO: TopicPostDTO) {
        if(topicDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)
    }
}
