package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Meeting
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingRepository : CrudRepository<Meeting, Long> {
    @Query("SELECT * FROM meetings", nativeQuery = true)
    fun getAllMeetings(): List<Meeting>

    @Query("SELECT * FROM meetings LIMIT 1", nativeQuery = true)
    fun getFirstMeeting(): Meeting
}