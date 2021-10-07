package com.karbal.tutortek.services

import com.karbal.tutortek.entities.UserProfile
import org.springframework.stereotype.Service
import com.karbal.tutortek.repositories.UserProfileRepository

@Service
class UserProfileService(val database: UserProfileRepository) {

    fun getAllUserProfiles(): List<UserProfile> = database.getAllUserProfiles()

    fun saveUserProfile(userProfile: UserProfile) = database.save(userProfile)

    fun deleteUserProfile(id: Long) = database.deleteById(id)

    fun getUserProfile(id: Long) = database.findById(id)

    fun getFirstUserProfile() = database.getFirstUserProfile()

    fun clearUserProfiles() = database.deleteAll()
}
