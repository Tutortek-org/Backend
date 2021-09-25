package com.karbal.tutortek.dto.meetingDTO

import java.sql.Date

data class MeetingPostDTO(
    var name: String,
    var date: Date,
    var maxAttendants: Int,
    var address: String,
    var description: String
)
