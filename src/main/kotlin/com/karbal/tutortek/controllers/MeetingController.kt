package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.meetingDTO.MeetingGetDTO
import com.karbal.tutortek.dto.meetingDTO.MeetingPostDTO
import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.utils.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class MeetingController(
    val meetingService: MeetingService,
    val topicService: TopicService) {

    @GetMapping("/topics/{topicId}/meetings")
    fun getAllMeetings(@PathVariable topicId: Long): List<MeetingGetDTO> {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        return topic.get().meetings.map { m -> MeetingGetDTO(m) }
    }

    @GetMapping("/topics/{topicId}/meetings/{meetingId}")
    fun getMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long): MeetingGetDTO {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)
        return MeetingGetDTO(meeting)
    }

    @PostMapping("/topics/{topicId}/meetings")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMeeting(@PathVariable topicId: Long, @RequestBody meetingDTO: MeetingPostDTO): MeetingGetDTO {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = Meeting(meetingDTO)
        meeting.topic = topic.get()
        return MeetingGetDTO(meetingService.saveMeeting(meeting))
    }

    @DeleteMapping("/topics/{topicId}/meetings/{meetingId}")
    fun deleteMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)
        meeting.id?.let { meetingService.deleteMeeting(it) }
    }

    @PutMapping("/topics/{topicId}/meetings/{meetingId}")
    fun updateMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long, @RequestBody meetingDTO: MeetingPostDTO) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)
        val meetingFromDto = Meeting(meetingDTO)
        meetingFromDto.id = meetingId
        meetingFromDto.topic = topic.get()
        meetingFromDto.learningMaterials = meeting.learningMaterials
        meetingFromDto.payments = meeting.payments
        meetingService.saveMeeting(meetingFromDto)
    }
}
