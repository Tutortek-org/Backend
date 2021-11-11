package com.karbal.tutortek.dto.userProfileDTO

import com.karbal.tutortek.entities.UserProfile
import java.sql.Date

data class UserProfileGetDTO(
    val id: Long?,
    val firstName: String,
    val lastName: String,
    val rating: Float,
    val birthDate: Date,
    val description: String,
    val topicCount: Long
){
    constructor(userProfile: UserProfile, topicCount: Long) : this(
        userProfile.id,
        userProfile.firstName,
        userProfile.lastName,
        userProfile.rating,
        userProfile.birthDate,
        userProfile.description,
        topicCount
    )
}
