package com.karbal.tutortek.dto.userDTO

import java.sql.Date

data class UserPostDTO(
    var firstName: String,
    var lastName: String,
    var rating: Float,
    var birthDate: Date
)
