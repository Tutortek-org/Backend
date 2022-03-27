package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.meetingDTO.MeetingGetDTO
import com.karbal.tutortek.dto.meetingDTO.MeetingPostDTO
import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.meetingDTO.PersonalMeetingGetDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.sql.Date
import javax.servlet.http.HttpServletRequest

@RestController
class MeetingController(
    private val meetingService: MeetingService,
    private val topicService: TopicService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userService: UserService
) {

    @GetMapping("topics/{topicId}/meetings")
    fun getAllMeetings(@PathVariable topicId: Long): List<MeetingGetDTO> {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        return topic.get().meetings
            .filter { m -> m.date > Date(System.currentTimeMillis()) }
            .map { m -> MeetingGetDTO(m) }
    }

    @GetMapping("topics/{topicId}/meetings/{meetingId}")
    fun getMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long): MeetingGetDTO {
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)
        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        return MeetingGetDTO(meeting)
    }

    @GetMapping("meetings/personal")
    fun getPersonalMeetings(request: HttpServletRequest): List<PersonalMeetingGetDTO> {
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)

        val userId = claims?.get("uid").toString().toLong()
        val user = userService.getUserById(userId)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userFromDatabase = user.get()

        return userFromDatabase.payments
            .filter { m -> m.date > Date(System.currentTimeMillis()) }
            .map { p -> PersonalMeetingGetDTO(p.meeting) }
    }

    @PostMapping("topics/{topicId}/meetings")
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

    @DeleteMapping("topics/{topicId}/meetings/{meetingId}")
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

    @PutMapping("topics/{topicId}/meetings/{meetingId}")
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

        if(meetingDTO.price < BigDecimal.ZERO)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NEGATIVE_PRICE)
    }
}
