package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.userProfileDTO.UserProfileGetDTO
import com.karbal.tutortek.dto.userProfileDTO.UserProfilePostDTO
import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.userProfileDTO.UserProfilePutDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.services.TopicService
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("profiles")
class UserProfileController(
    val userProfileService: UserProfileService,
    val userService: UserService,
    val topicService: TopicService,
    val jwtTokenUtil: JwtTokenUtil
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserProfile(@RequestBody userProfileDTO: UserProfilePostDTO,
                       request: HttpServletRequest
    ): UserProfileGetDTO {
        verifyPostDto(userProfileDTO)
        val userProfile = UserProfile(userProfileDTO)
        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val email = claims?.get("sub").toString()
        val user = userService.getUserByEmail(email)
        userProfile.user = user
        return UserProfileGetDTO(userProfileService.saveUserProfile(userProfile), 0)
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
    fun getAllUserProfiles() = userProfileService.getAllUserProfiles().map { up ->
        val topicCount = up.id?.let { topicService.getTopicCountBelongingToUser(it) }
        if (topicCount != null) UserProfileGetDTO(up, topicCount)
    }

    @GetMapping("{id}")
    fun getUserProfile(@PathVariable id: Long): UserProfileGetDTO? {
        val userProfile = userProfileService.getUserProfile(id)
        if(userProfile.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userProfileInDatabase = userProfile.get()
        val topicCount = userProfileInDatabase.id?.let { topicService.getTopicCountBelongingToUser(it) }
        return topicCount?.let { UserProfileGetDTO(userProfileInDatabase, it) }
    }

    @PutMapping("{id}")
    fun updateUserProfile(@PathVariable id: Long, @RequestBody userProfileDTO: UserProfilePutDTO): UserProfileGetDTO? {
        verifyPutDto(userProfileDTO)
        val userProfile = UserProfile(userProfileDTO)
        val userProfileInDatabase = userProfileService.getUserProfile(id)

        if(userProfileInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        val extractedUserProfile = userProfileInDatabase.get()
        extractedUserProfile.copy(userProfile)

        val topicCount = extractedUserProfile.id?.let { topicService.getTopicCountBelongingToUser(it) }
        return topicCount?.let { UserProfileGetDTO(userProfileService.saveUserProfile(extractedUserProfile), it) }
    }

    fun verifyPostDto(userProfileDTO: UserProfilePostDTO) {
        if(userProfileDTO.firstName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.FIRST_NAME_EMPTY)

        if(userProfileDTO.lastName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LAST_NAME_EMPTY)

        if(userProfileDTO.birthDate.after(Date(System.currentTimeMillis())))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BIRTH_DATE_AFTER_TODAY)
    }

    fun verifyPutDto(userProfilePutDTO: UserProfilePutDTO) {
        if(userProfilePutDTO.firstName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.FIRST_NAME_EMPTY)

        if(userProfilePutDTO.lastName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LAST_NAME_EMPTY)

        if(userProfilePutDTO.birthDate.after(Date(System.currentTimeMillis())))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BIRTH_DATE_AFTER_TODAY)

        if(userProfilePutDTO.rating < 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NEGATIVE_RATING)
    }
}
