package com.karbal.tutortek.security

enum class Role {
    ADMIN,
    TUTOR,
    STUDENT;

    companion object {
        const val ADMIN_ANNOTATION = "ROLE_ADMIN"
        const val TUTOR_ANNOTATION = "ROLE_TUTOR"
        const val STUDENT_ANNOTATION = "ROLE_STUDENT"
    }
}
