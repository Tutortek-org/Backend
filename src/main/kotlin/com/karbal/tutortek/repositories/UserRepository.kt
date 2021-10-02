package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : CrudRepository<User, Long> {
    @Query("SELECT * FROM users", nativeQuery = true)
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users LIMIT 1", nativeQuery = true)
    fun getFirstUser(): User

    @Modifying
    @Transactional
    @Query("TRUNCATE TABLE users", nativeQuery = true)
    fun clearUsers()
}
