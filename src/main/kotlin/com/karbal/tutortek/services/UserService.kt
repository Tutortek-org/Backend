package com.karbal.tutortek.services

import com.karbal.tutortek.entities.User
import org.springframework.stereotype.Service
import com.karbal.tutortek.repositories.UserRepository

@Service
class UserService(val database: UserRepository) {

    fun getAllUsers(): List<User> = database.getAllUsers()

    fun post(user: User){
        database.save(user)
    }
}
