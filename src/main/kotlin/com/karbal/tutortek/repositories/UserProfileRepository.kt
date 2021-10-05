package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.UserProfile
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : CrudRepository<UserProfile, Long> {
    @Query("SELECT * FROM user_profiles", nativeQuery = true)
    fun getAllUserProfiles(): List<UserProfile>

    @Query("SELECT * FROM user_profiles LIMIT 1", nativeQuery = true)
    fun getFirstUserProfile(): UserProfile
}
