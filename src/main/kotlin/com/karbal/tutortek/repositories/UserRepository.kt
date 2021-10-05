package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long> {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    fun findByEmail(@Param("email") email: String): User

    @Query("SELECT * FROM users LIMIT 1", nativeQuery = true)
    fun getFirstUser(): User
}
