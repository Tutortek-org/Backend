package com.karbal.tutortek.controllers

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserController(val userService: UserService) {

    @GetMapping
    fun index() = "TESTAS"

    @PostMapping("/users/add")
    fun addUser(@RequestBody user: User){
        userService.createUser(user)
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long){
        val user = userService.getUser(id)
        if(user.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        userService.deleteUser(id)
    }

    @GetMapping("/users/all")
    fun getAllUsers() = userService.getAllUsers()

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): Optional<User> {
        val user = userService.getUser(id)
        if(user.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user
    }

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User){
        val userInDatabase = userService.getUser(id)
        if(userInDatabase.isEmpty) throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        val extractedUser = userInDatabase.get()
        extractedUser.copy(user)
        userService.updateUser(extractedUser)
    }
}
