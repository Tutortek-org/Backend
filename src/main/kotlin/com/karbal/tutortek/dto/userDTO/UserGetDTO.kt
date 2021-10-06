package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.entities.User

data class UserGetDTO(
    val username: String
) {
    constructor(user: User) : this(
        user.email
    )
}
