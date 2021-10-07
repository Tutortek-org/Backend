package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.meetingDTO.MeetingGetDTO
import com.karbal.tutortek.dto.meetingDTO.MeetingPostDTO
import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.security.Role
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Date

@RestController
@RequestMapping("topics/{topicId}/meetings")
class MeetingController(
    val meetingService: MeetingService,
    val topicService: TopicService) {

    @GetMapping
    fun getAllMeetings(@PathVariable topicId: Long): List<MeetingGetDTO> {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        return topic.get().meetings.map { m -> MeetingGetDTO(m) }
    }

    @GetMapping("{meetingId}")
    fun getMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long): MeetingGetDTO {
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        return MeetingGetDTO(meeting)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun addMeeting(@PathVariable topicId: Long, @RequestBody meetingDTO: MeetingPostDTO): MeetingGetDTO {
        verifyDto(meetingDTO)
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val meeting = Meeting(meetingDTO)
        meeting.topic = topic.get()
        return MeetingGetDTO(meetingService.saveMeeting(meeting))
    }

    @DeleteMapping("{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun deleteMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)
        meeting.id?.let { meetingService.deleteMeeting(it) }
    }

    @PutMapping("{meetingId}")
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun updateMeeting(@PathVariable topicId: Long,
                      @PathVariable meetingId: Long,
                      @RequestBody meetingDTO: MeetingPostDTO): MeetingGetDTO {
        verifyDto(meetingDTO)
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val meetingFromDto = Meeting(meetingDTO)
        meetingFromDto.id = meetingId
        meetingFromDto.topic = topic.get()
        meetingFromDto.learningMaterials = meeting.learningMaterials
        meetingFromDto.payments = meeting.payments

        return MeetingGetDTO(meetingService.saveMeeting(meetingFromDto))
    }

    fun verifyDto(meetingDTO: MeetingPostDTO) {
        if(meetingDTO.name.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NAME_EMPTY)

        if(meetingDTO.address.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.ADDRESS_EMPTY)

        if(meetingDTO.description.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DESCRIPTION_EMPTY)

        if(meetingDTO.maxAttendants < 1)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.TOO_FEW_MAX_ATTENDANTS)

        if(meetingDTO.date.before(Date(System.currentTimeMillis())))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.DATE_BEFORE_TODAY)
    }
}
