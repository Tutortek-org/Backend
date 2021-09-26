package com.karbal.tutortek.controllers

import com.karbal.tutortek.dto.userDTO.UserGetDTO
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserController(val userService: UserService) {

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@RequestBody userDTO: UserPostDTO): UserGetDTO {
        val user = User(userDTO)
        return UserGetDTO(userService.saveUser(user))
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long){
        val user = userService.getUser(id)
        if(user.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        userService.deleteUser(id)
    }

    @GetMapping("/users")
    fun getAllUsers() = userService.getAllUsers().map { u -> UserGetDTO(u) }

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): UserGetDTO {
        val user = userService.getUser(id)
        if(user.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return UserGetDTO(user.get())
    }

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody userDTO: UserPostDTO){
        val user = User(userDTO)
        val userInDatabase = userService.getUser(id)
        if(userInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        val extractedUser = userInDatabase.get()
        extractedUser.copy(user)
        userService.saveUser(extractedUser)
    }
}
