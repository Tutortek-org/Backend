package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.entities.User
import java.sql.Date

data class UserGetDTO(
    var firstName: String,
    var lastName: String,
    var rating: Float,
    var birthDate: Date
){
    constructor(user: User) : this(
        user.firstName,
        user.lastName,
        user.rating,
        user.birthDate
    )
}
