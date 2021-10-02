package com.karbal.tutortek.services

import com.karbal.tutortek.entities.Topic
import com.karbal.tutortek.repositories.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class TopicService(val database: TopicRepository) {

    fun getAllTopics(): List<Topic> = database.getAllTopics()

    fun saveTopic(topic: Topic) = database.save(topic)

    fun deleteTopic(id: Long) = database.deleteById(id)

    fun getTopic(id: Long) = database.findById(id)

    fun getFirstTopic() = database.getFirstTopic()

    fun clearTopics() = database.deleteAll()
}
