package com.karbal.tutortek.services

import com.karbal.tutortek.entities.User
import com.karbal.tutortek.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val database: UserRepository) {

    fun getFirstUser() = database.getFirstUser()

    fun clearUsers() = database.deleteAll()

    fun saveUser(user: User) = database.save(user)

    fun getUserCountByEmail(email: String) = database.getUserCountByEmail(email)
}
