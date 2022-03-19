package com.karbal.tutortek.dto.meetingDTO

import java.math.BigDecimal
import java.sql.Date

data class MeetingPostDTO(
    val name: String,
    val date: Date,
    val maxAttendants: Int,
    val address: String,
    val description: String,
    val price: BigDecimal
)
