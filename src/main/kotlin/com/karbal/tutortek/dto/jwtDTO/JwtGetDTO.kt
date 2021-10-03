package com.karbal.tutortek.dto.jwtDTO

import java.io.Serializable

data class JwtGetDTO(
    val token: String,
    val serialVersionUID: Long = -8091879091924046844L
) : Serializable
