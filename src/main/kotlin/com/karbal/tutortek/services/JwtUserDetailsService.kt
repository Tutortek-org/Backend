package com.karbal.tutortek.services

import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.repositories.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    val userRepository: UserRepository,
    val bcryptEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        if(username == "javainuse")
            return User("javainuse", "\$2a\$10\$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", arrayListOf())
        else throw UsernameNotFoundException("User not found with username: $username")
    }

    fun save(userPostDTO: UserPostDTO): com.karbal.tutortek.entities.User {
        userPostDTO.password = bcryptEncoder.encode(userPostDTO.password)
        val user = com.karbal.tutortek.entities.User(userPostDTO)
        return userRepository.save(user)
    }
}
