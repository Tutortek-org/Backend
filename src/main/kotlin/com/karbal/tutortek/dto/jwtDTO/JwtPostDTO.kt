package com.karbal.tutortek.dto.jwtDTO

import java.io.Serializable

data class JwtPostDTO(
    val email: String = "",
    val password: String = ""
) : Serializable
