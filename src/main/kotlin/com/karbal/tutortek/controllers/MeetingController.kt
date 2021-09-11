package com.karbal.tutortek.controllers

import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class MeetingController(val meetingService: MeetingService) {

    @GetMapping("/meetings/all")
    fun getAllMeetings() = meetingService.getAllMeetings()

    @GetMapping("/meetings/{id}")
    fun getMeeting(@PathVariable id: Long): Optional<Meeting> {
        val meeting = meetingService.getMeeting(id)
        if(meeting.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        return meeting
    }

    @PostMapping("/meetings/add")
    fun addMeeting(@RequestBody meeting: Meeting) = meetingService.saveMeeting(meeting)

    @DeleteMapping("/meetings/{id}")
    fun deleteMeeting(@PathVariable id: Long) {
        val meeting = meetingService.getMeeting(id)
        if(meeting.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        meetingService.deleteMeeting(id)
    }

    @PutMapping("/meetings/{id}")
    fun updateMeeting(@PathVariable id: Long, @RequestBody meeting: Meeting){
        val meetingInDatabase = meetingService.getMeeting(id)
        if(meetingInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val extractedMeeting = meetingInDatabase.get()
        extractedMeeting.copy(meeting)
        meetingService.saveMeeting(extractedMeeting)
    }
}
