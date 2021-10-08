package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.entities.User
import java.sql.Date

data class UserGetDTO(
    val id: Long?,
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: Date
){
    constructor(user: User) : this(
        user.id,
        user.firstName,
        user.lastName,
        user.rating,
        user.birthDate
    )
}
