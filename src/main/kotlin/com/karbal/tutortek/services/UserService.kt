package com.karbal.tutortek.services

import com.karbal.tutortek.entities.User
import org.springframework.stereotype.Service
import com.karbal.tutortek.repositories.UserRepository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class UserService(val database: UserRepository) {

    fun getAllUsers(): List<User> = database.getAllUsers()

    fun saveUser(user: User) = database.save(user)

    fun deleteUser(id: Long) = database.deleteById(id)

    fun getUser(id: Long) = database.findById(id)

    fun getFirstUser() = database.getFirstUser()

    fun clearUsers() = database.deleteAll()
}
