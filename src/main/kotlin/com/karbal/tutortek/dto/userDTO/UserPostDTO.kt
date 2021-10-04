package com.karbal.tutortek.dto.userDTO

import java.sql.Date

data class UserPostDTO(
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: Date,
    val email: String,
    val password: String
)
