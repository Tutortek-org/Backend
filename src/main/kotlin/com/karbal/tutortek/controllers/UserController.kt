package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.userDTO.UserGetDTO
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import com.karbal.tutortek.utils.ApiErrorSlug
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Date

@RestController
class UserController(val userService: UserService) {

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@RequestBody userDTO: UserPostDTO): UserGetDTO {
        verifyDto(userDTO)
        val user = User(userDTO)
        return UserGetDTO(userService.saveUser(user))
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long){
        val user = userService.getUser(id)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        userService.deleteUser(id)
    }

    @GetMapping("/users")
    fun getAllUsers() = userService.getAllUsers().map { u -> UserGetDTO(u) }

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): UserGetDTO {
        val user = userService.getUser(id)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        return UserGetDTO(user.get())
    }

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody userDTO: UserPostDTO){
        verifyDto(userDTO)
        val user = User(userDTO)
        val userInDatabase = userService.getUser(id)

        if(userInDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)

        val extractedUser = userInDatabase.get()
        extractedUser.copy(user)
        userService.saveUser(extractedUser)
    }

    fun verifyDto(userDTO: UserPostDTO) {
        if(userDTO.firstName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.FIRST_NAME_EMPTY)

        if(userDTO.lastName.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.LAST_NAME_EMPTY)

        if(userDTO.birthDate.after(Date(System.currentTimeMillis())))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.BIRTH_DATE_AFTER_TODAY)

        if(userDTO.rating < 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.NEGATIVE_RATING)
    }
}
