package com.karbal.tutortek.dto.userProfileDTO

import java.sql.Date

data class UserProfilePutDTO(
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val rating: Float,
    val description: String
)
