package com.karbal.tutortek.services

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.repositories.MeetingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class MeetingService(
    val database: MeetingRepository,
    val entityManager: EntityManager) {

    fun getAllMeetings(): List<Meeting> = database.getAllMeetings()

    fun saveMeeting(meeting: Meeting) = database.save(meeting)

    fun deleteMeeting(id: Long) = database.deleteById(id)

    fun getMeeting(id: Long) = database.findById(id)

    fun getFirstMeeting() = database.getFirstMeeting()

    @Transactional
    fun clearMeetings() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate()
        database.clearMeetings()
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate()
    }
}