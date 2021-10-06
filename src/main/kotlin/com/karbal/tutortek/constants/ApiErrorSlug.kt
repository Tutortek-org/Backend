package com.karbal.tutortek.constants

class ApiErrorSlug {
    companion object {

        // ID Verification errors
        const val USER_NOT_FOUND = "User not found"
        const val TOPIC_NOT_FOUND = "Topic not found"
        const val PAYMENT_NOT_FOUND = "Payment not found"
        const val MEETING_NOT_FOUND = "Meeting not found"
        const val MATERIAL_NOT_FOUND = "Learning material not found"

        // UserProfile DTO validation errors
        const val FIRST_NAME_EMPTY = "First name field is empty"
        const val LAST_NAME_EMPTY = "Last name field is empty"
        const val BIRTH_DATE_AFTER_TODAY = "The provided birth date is after today"
        const val NEGATIVE_RATING = "A negative rating is not allowed"

        // Meeting DTO validation errors
        const val ADDRESS_EMPTY = "Address field is empty"
        const val TOO_FEW_MAX_ATTENDANTS = "There must be at least 1 attendant allowed"
        const val DATE_BEFORE_TODAY = "Meeting date must be after today"

        // Payment DTO validation errors
        const val NEGATIVE_PRICE = "Price cannot be negative"

        // Learning material DTO validation errors
        const val LINK_EMPTY = "Link field is empty"

        // JWT errors
        const val ACCOUNT_DISABLED = "Account is disabled"
        const val INVALID_CREDENTIALS = "Invalid credentials"

        // User errors
        const val USERNAME_TOO_SHORT = "Username must be at least 5 characters"
        const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters"

        // Common DTO validation errors
        const val NAME_EMPTY = "Name field is empty"
        const val DESCRIPTION_EMPTY = "Description field is empty"
    }
}
