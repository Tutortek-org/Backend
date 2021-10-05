package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.userProfileDTO.UserProfileGetDTO
import com.karbal.tutortek.dto.userProfileDTO.UserProfilePostDTO
import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Date

@RestController
@RequestMapping("profiles")
class UserProfileController(val userProfileService: UserProfileService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserProfile(@RequestBody userProfileDTO: UserProfilePostDTO): UserProfileGetDTO {
        verifyDto(userProfileDTO)
        val userProfile = UserProfile(userProfileDTO)
        return UserProfileGetDTO(userProfileService.saveUserProfile(userProfile))
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUserProfile(@PathVariable id: Long){
        val userProfile = userProfileService.getUserProfile(id)
        if(userProfile.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        userProfileService.deleteUserProfile(id)
    }

    @GetMapping
    fun getAllUserProfiles() = userProfileService.getAllUserProfiles().map { up -> UserProfileGetDTO(up) }

    @GetMapping("{id}")
    fun getUserProfile(@PathVariable id: Long): UserProfileGetDTO {
        val userProfile = userProfileService.getUserProfile(id)
        if(userProfile.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        return UserProfileGetDTO(userProfile.get())
    }

    @PutMapping("{id}")
    fun updateUserProfile(@PathVariable id: Long, @RequestBody userProfileDTO: UserProfilePostDTO): UserProfileGetDTO {
        verifyDto(userProfileDTO)
        val userProfile = UserProfile(userProfileDTO)
        val userProfileInDatabase = userProfileService.getUserProfile(id)

        if(userProfileInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        val extractedUserProfile = userProfileInDatabase.get()
        extractedUserProfile.copy(userProfile)
        return UserProfileGetDTO(userProfileService.saveUserProfile(extractedUserProfile))
    }

    fun verifyDto(userProfileDTO: UserProfilePostDTO) {
        if(userProfileDTO.firstName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.FIRST_NAME_EMPTY)

        if(userProfileDTO.lastName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LAST_NAME_EMPTY)

        if(userProfileDTO.birthDate.after(Date(System.currentTimeMillis())))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BIRTH_DATE_AFTER_TODAY)

        if(userProfileDTO.rating < 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NEGATIVE_RATING)
    }
}
