package com.karbal.tutortek.dto.meetingDTO

import com.karbal.tutortek.entities.Meeting
import java.sql.Date

data class MeetingGetDTO(
    var name: String,
    var date: Date,
    var maxAttendants: Int,
    var address: String,
    var description: String
){
    constructor(meeting: Meeting) : this(
        meeting.name,
        meeting.date,
        meeting.maxAttendants,
        meeting.address,
        meeting.description
    )
}
