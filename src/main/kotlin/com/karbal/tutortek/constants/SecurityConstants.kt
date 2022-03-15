package com.karbal.tutortek.constants

object SecurityConstants {
    const val AUTHORIZATION_HEADER = "Authorization"
    const val CLAIMS_ATTRIBUTE = "claims"
    const val TOKEN_BEGINNING = "Bearer "

    const val TOKEN_EXPIRATION = 9_000_000
    const val REFRESH_EXPIRATION = 18_000_000

    const val LOGIN_ENDPOINT = "/login"
    const val REGISTER_ENDPOINT = "/register"
    const val REFRESH_ENDPOINT = "/refresh"
}
