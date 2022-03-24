package com.karbal.tutortek.controllers

import com.amazonaws.util.IOUtils
import com.karbal.tutortek.dto.userProfileDTO.UserProfileGetDTO
import com.karbal.tutortek.dto.userProfileDTO.UserProfilePostDTO
import com.karbal.tutortek.entities.UserProfile
import com.karbal.tutortek.services.UserProfileService
import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.userProfileDTO.UserProfilePutDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.S3Utils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.sql.Date
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("profiles")
class UserProfileController(
    private val userProfileService: UserProfileService,
    private val userService: UserService,
    private val jwtTokenUtil: JwtTokenUtil
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
        return UserProfileGetDTO(userProfileService.saveUserProfile(userProfile))
    }

    @PutMapping("/picture")
    fun addProfilePicture(@RequestParam photo: MultipartFile, request: HttpServletRequest) {
        val claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val email = claims?.get("sub").toString()
        S3Utils.uploadFile("pfp_$email.png", photo.inputStream)
    }

    @GetMapping("{id}/picture", produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE])
    fun getProfilePicture(@PathVariable id: Long, request: HttpServletRequest): ByteArray? {
        val userProfile = userProfileService.getUserProfile(id)
        if(userProfile.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val file = S3Utils.downloadFile("pfp_${userProfile.get().user.email}.png")
        return IOUtils.toByteArray(file)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUserProfile(@PathVariable id: Long) {
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
        val userProfileInDatabase = userProfile.get()
        return UserProfileGetDTO(userProfileInDatabase)
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

        return UserProfileGetDTO(userProfileService.saveUserProfile(extractedUserProfile))
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
