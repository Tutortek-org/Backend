package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.Meeting
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface MeetingRepository : CrudRepository<Meeting, Long> {
    @Query("SELECT * FROM meetings", nativeQuery = true)
    fun getAllMeetings(): List<Meeting>

    @Query("SELECT * FROM meetings LIMIT 1", nativeQuery = true)
    fun getFirstMeeting(): Meeting

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE meetings", nativeQuery = true)
    fun clearMeetings()
}