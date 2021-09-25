package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.MeetingDTO
import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class MeetingController(val meetingService: MeetingService,
                        val topicService: TopicService) {

    @GetMapping("/meetings/all")
    fun getAllMeetings() = meetingService.getAllMeetings()

    @GetMapping("/meetings/{id}")
    fun getMeeting(@PathVariable id: Long): Optional<Meeting> {
        val meeting = meetingService.getMeeting(id)
        if(meeting.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        return meeting
    }

    @PostMapping("/meetings/add")
    fun addMeeting(@RequestBody meetingDTO: MeetingDTO): Meeting {
        val meeting = convertDtoToEntity(meetingDTO)
        return meetingService.saveMeeting(meeting)
    }

    @DeleteMapping("/meetings/{id}")
    fun deleteMeeting(@PathVariable id: Long) {
        val meeting = meetingService.getMeeting(id)
        if(meeting.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        meetingService.deleteMeeting(id)
    }

    @PutMapping("/meetings/{id}")
    fun updateMeeting(@PathVariable id: Long, @RequestBody meetingDTO: MeetingDTO){
        val meeting = convertDtoToEntity(meetingDTO)
        val meetingInDatabase = meetingService.getMeeting(id)
        if(meetingInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "Meeting not found")
        val extractedMeeting = meetingInDatabase.get()
        extractedMeeting.copy(meeting)
        meetingService.saveMeeting(extractedMeeting)
    }

    fun convertDtoToEntity(meetingDTO: MeetingDTO): Meeting {
        val meeting = Meeting()
        meeting.date = meetingDTO.date
        meeting.address = meetingDTO.address
        meeting.description = meetingDTO.description
        meeting.name = meetingDTO.name
        meeting.maxAttendants = meetingDTO.maxAttendants
        meeting.topic = topicService.getTopic(meetingDTO.topicId).get()
        return meeting
    }
}
