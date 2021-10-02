package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Topic
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TopicRepository : CrudRepository<Topic, Long> {
    @Query("SELECT * FROM topics", nativeQuery = true)
    fun getAllTopics(): List<Topic>

    @Query("SELECT * FROM topics LIMIT 1", nativeQuery = true)
    fun getFirstTopic(): Topic

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE topics", nativeQuery = true)
    fun clearTopics()
}
