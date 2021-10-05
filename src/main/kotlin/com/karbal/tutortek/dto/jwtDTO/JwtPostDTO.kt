package com.karbal.tutortek.dto.jwtDTO

import java.io.Serializable

data class JwtPostDTO(
    val username: String = "",
    val password: String = ""
) : Serializable
