package com.karbal.tutortek.constants

class SecurityConstants {
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val TOKEN_BEGINNING = "Bearer "
        const val TOKEN_VALIDITY_TIME = 18_000_000
        const val LOGIN_ENDPOINT = "/login"
        const val REGISTER_ENDPOINT = "/register"
    }
}
