package com.karbal.tutortek.dto.userDTO

import java.sql.Date

data class UserGetDTO(
    var firstName: String,
    var lastName: String,
    var rating: Float,
    var creationDate: Date
)
