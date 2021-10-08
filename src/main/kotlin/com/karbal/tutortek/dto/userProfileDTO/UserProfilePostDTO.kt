package com.karbal.tutortek.dto.userProfileDTO

import java.sql.Date

data class UserProfilePostDTO(
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: Date
)
