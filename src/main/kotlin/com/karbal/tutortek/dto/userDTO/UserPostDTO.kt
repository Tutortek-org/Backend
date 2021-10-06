package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.security.Role

data class UserPostDTO(
    val username: String,
    var password: String,
    val role: Role
)
