package com.karbal.tutortek.services

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.repositories.MeetingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class MeetingService(val database: MeetingRepository) {

    fun getAllMeetings(): List<Meeting> = database.getAllMeetings()

    fun saveMeeting(meeting: Meeting) = database.save(meeting)

    fun deleteMeeting(id: Long) = database.deleteById(id)

    fun getMeeting(id: Long) = database.findById(id)

    fun getFirstMeeting() = database.getFirstMeeting()

    fun clearMeetings() = database.deleteAll()
}
