package com.karbal.tutortek.dto

import java.sql.Date

data class MeetingDTO(
    var name: String,
    var date: Date,
    var maxAttendants: Int,
    var address: String,
    var description: String,
    var topicId: Long
)
