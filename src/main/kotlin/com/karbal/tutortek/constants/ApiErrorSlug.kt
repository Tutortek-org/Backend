package com.karbal.tutortek.constants

object ApiErrorSlug {

    // ID Verification errors
    const val USER_NOT_FOUND = "User not found"
    const val TOPIC_NOT_FOUND = "Topic not found"
    const val PAYMENT_NOT_FOUND = "Payment not found"
    const val MEETING_NOT_FOUND = "Meeting not found"
    const val MATERIAL_NOT_FOUND = "Learning material not found"
    const val BUG_REPORT_NOT_FOUND = "Bug report not found"
    const val USER_REPORT_NOT_FOUND = "User report not found"
    const val USER_FORBIDDEN_FROM_ALTERING = "User is forbidden from altering this resource"
    const val USER_ALREADY_SIGNED_UP = "User is already signed up for this meeting"
    const val SELF_RATE = "You cannot rate yourself"

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
    const val EMAIL_NOT_FOUND = "User not found with email: "

    // User errors
    const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters"
    const val EMAIL_NOT_VALID = "Entered email is not in a valid format"
    const val USER_ALREADY_EXISTS = "A user with that email is already registered"
    const val ROLE_NOT_FOUND = "Role not found with given id"
    const val ADMIN_GRANT_NOT_ALLOWED = "Only an admin can grant the role of an admin"
    const val USER_IS_BANNED = "User is banned"

    // Common DTO validation errors
    const val NAME_EMPTY = "Name field is empty"
    const val DESCRIPTION_EMPTY = "Description field is empty"

    // AWS errors
    const val NO_PHOTO_EXISTS = "The user does not have a photo uploaded"
    const val EMPTY_DEVICE_TOKEN = "A device token must be provided"
    const val BLANK_NOTIFICATION_TITLE = "Notification title cannot be blank"
    const val BLANK_NOTIFICATION_CONTENT = "Notification content cannot be blank"
}
