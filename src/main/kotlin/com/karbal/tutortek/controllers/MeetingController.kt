package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.meetingDTO.MeetingGetDTO
import com.karbal.tutortek.dto.meetingDTO.MeetingPostDTO
import com.karbal.tutortek.entities.Meeting
import com.karbal.tutortek.services.MeetingService
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.meetingDTO.PersonalMeetingGetDTO
import com.karbal.tutortek.dto.userProfileDTO.UserProfileGetDTO
import com.karbal.tutortek.entities.Topic
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
    fun getAllMeetings(@PathVariable topicId: Long, request: HttpServletRequest): List<MeetingGetDTO> {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val userId = claims?.get("uid").toString().toLong()
        val user = userService.getUserById(userId)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userFromDatabase = user.get()

        return topic.get().meetings
            .filter { m ->
                m.date >= Date(System.currentTimeMillis())
                        && m.payments.size < m.maxAttendants
                        && !userFromDatabase.payments.any { p -> p.meeting.id == m.id }
            }
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
            .filter { p -> p.meeting.date >= Date(System.currentTimeMillis()) }
            .map { p -> PersonalMeetingGetDTO(p.meeting) }
    }

    @GetMapping("meetings/{id}/registered")
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun getRegisteredUsers(@PathVariable id: Long, request: HttpServletRequest): List<UserProfileGetDTO?> {
        val meeting = meetingService.getMeeting(id)
        if(meeting.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val meetingFromDatabase = meeting.get()
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)

        val profileId = claims?.get("pid").toString().toLong()
        if(meetingFromDatabase.topic.userProfile.id != profileId)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.USER_FORBIDDEN_FROM_ALTERING)

        return meetingFromDatabase.payments.map { p -> p.user.userProfile?.let { UserProfileGetDTO(it) } }
    }

    @PostMapping("topics/{topicId}/meetings")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun addMeeting(@PathVariable topicId: Long,
                   @RequestBody meetingDTO: MeetingPostDTO,
                   request: HttpServletRequest): MeetingGetDTO {
        verifyDto(meetingDTO)

        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = Meeting(meetingDTO)
        meeting.topic = topicFromDatabase
        return MeetingGetDTO(meetingService.saveMeeting(meeting))
    }

    @DeleteMapping("topics/{topicId}/meetings/{meetingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun deleteMeeting(@PathVariable topicId: Long, @PathVariable meetingId: Long, request: HttpServletRequest) {
        val topic = topicService.getTopic(topicId)
        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = topicFromDatabase.meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        meeting.id?.let { meetingService.deleteMeeting(it) }
    }

    @PutMapping("topics/{topicId}/meetings/{meetingId}")
    @Secured(Role.ADMIN_ANNOTATION, Role.TUTOR_ANNOTATION)
    fun updateMeeting(@PathVariable topicId: Long,
                      @PathVariable meetingId: Long,
                      @RequestBody meetingDTO: MeetingPostDTO,
                      request: HttpServletRequest): MeetingGetDTO {
        verifyDto(meetingDTO)
        val topic = topicService.getTopic(topicId)

        if(topic.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.TOPIC_NOT_FOUND)

        val topicFromDatabase = topic.get()
        verifyUserProfileID(topicFromDatabase, request)

        val meeting = topic.get().meetings.find { m -> m.id == meetingId }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.MEETING_NOT_FOUND)

        val meetingFromDto = Meeting(meetingDTO)
        meetingFromDto.id = meetingId
        meetingFromDto.topic = topicFromDatabase
        meetingFromDto.learningMaterials = meeting.learningMaterials
        meetingFromDto.payments = meeting.payments

        return MeetingGetDTO(meetingService.saveMeeting(meetingFromDto))
    }

    private fun verifyUserProfileID(topic: Topic, request: HttpServletRequest) {
        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val profileId = claims?.get("pid").toString().toLong()
        if(topic.userProfile.id != profileId)
            throw ResponseStatusException(HttpStatus.FORBIDDEN, ApiErrorSlug.USER_FORBIDDEN_FROM_ALTERING)
    }

    private fun verifyDto(meetingDTO: MeetingPostDTO) {
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
