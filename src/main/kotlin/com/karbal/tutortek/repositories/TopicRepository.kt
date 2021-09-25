package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Topic
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : CrudRepository<Topic, Long> {
    @Query("SELECT * FROM topics", nativeQuery = true)
    fun getAllTopics(): List<Topic>
}
