package com.karbal.tutortek.dto.jwtDTO

import java.io.Serializable

data class JwtPostDTO(
    val username: String = "",
    val password: String = "",
    val serialVersionUID: Long = 5926468583005150707L
) : Serializable
