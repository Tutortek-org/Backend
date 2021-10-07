package com.karbal.tutortek.dto.userDTO

import com.karbal.tutortek.security.Role

data class UserPostDTO(
    val email: String,
    var password: String,
    val role: Role
)
