package com.karbal.tutortek.dto.meetingDTO

import com.karbal.tutortek.dto.topicDTO.TopicGetDTO
import com.karbal.tutortek.entities.Meeting
import java.math.BigDecimal
import java.sql.Date

data class PersonalMeetingGetDTO(
    val id: Long?,
    val name: String,
    val date: Date,
    val maxAttendants: Int,
    val address: String,
    val description: String,
    val price: BigDecimal,
    val profileId: Long?,
    val topic: TopicGetDTO
){
    constructor(meeting: Meeting) : this(
        meeting.id,
        meeting.name,
        meeting.date,
        meeting.maxAttendants,
        meeting.address,
        meeting.description,
        meeting.price,
        meeting.topic.userProfile.id,
        TopicGetDTO(meeting.topic)
    )
}
