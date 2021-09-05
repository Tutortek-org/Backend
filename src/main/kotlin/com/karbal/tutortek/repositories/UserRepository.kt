package com.karbal.tutortek.repositories

import com.karbal.tutortek.entities.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<User, Long>{
    @Query("SELECT * FROM users", nativeQuery = true)
    fun getAllUsers(): List<User>
}
