package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.TopicDTO
import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*


@RestController
class TopicController(val topicService: TopicService,
                      val userService: UserService) {

    @PostMapping("/topics/add")
    fun addTopic(@RequestBody topicDTO: TopicDTO): Topic {
        val topic = convertDtoToEntity(topicDTO)
        return topicService.saveTopic(topic)
    }

    @DeleteMapping("/topics/{id}")
    fun deleteTopic(@PathVariable id: Long){
        val topic = topicService.getTopic(id)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        topicService.deleteTopic(id)
    }

    @GetMapping("/topics/all")
    fun getAllTopics() = topicService.getAllTopics()

    @GetMapping("/topics/{id}")
    fun getTopic(@PathVariable id: Long): Optional<Topic> {
        val topic = topicService.getTopic(id)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        return topic
    }

    @PutMapping("/topics/{id}")
    fun updateTopic(@PathVariable id: Long, @RequestBody topicDTO: TopicDTO){
        val topic = convertDtoToEntity(topicDTO)
        val topicInDatabase = topicService.getTopic(id)
        if(topicInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found")
        val extractedTopic = topicInDatabase.get()
        extractedTopic.copy(topic)
        topicService.saveTopic(extractedTopic)
    }

    fun convertDtoToEntity(topicDTO: TopicDTO): Topic {
        val topic = Topic()
        topic.name = topicDTO.name
        topic.user = userService.getUser(topicDTO.userId).get()
        return topic
    }
}
