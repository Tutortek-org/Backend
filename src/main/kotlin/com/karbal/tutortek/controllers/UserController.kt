package com.karbal.tutortek.controllers

import com.karbal.tutortek.entities.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import com.karbal.tutortek.services.UserService
import org.springframework.web.bind.annotation.RequestBody

@RestController
class UserController(val userService: UserService) {

    @GetMapping
    fun index() = "TESTAS"

    @PostMapping("/users/add")
    fun addUser(@RequestBody user: User){
        userService.post(user)
    }

    @GetMapping("/users/all")
    fun getAllUsers() = userService.getAllUsers()
}
