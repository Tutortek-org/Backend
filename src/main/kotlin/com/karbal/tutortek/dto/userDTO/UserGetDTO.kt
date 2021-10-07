package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.entities.User

data class UserGetDTO(
    val id: Long?,
    val email: String
) {
    constructor(user: User) : this(
        user.id,
        user.email
    )
}
