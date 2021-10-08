package com.karbal.tutortek.services

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    override fun loadUserByUsername(email: String?): UserDetails {
        val user = email?.let { userRepository.findByEmail(it) }
            ?: throw UsernameNotFoundException(ApiErrorSlug.EMAIL_NOT_FOUND + email)

        val roles = user.roles.map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }
        return User(user.email, user.password, roles)
    }

    fun save(userPostDTO: UserPostDTO, roleEntity: RoleEntity): com.karbal.tutortek.entities.User {
        userPostDTO.password = bcryptEncoder.encode(userPostDTO.password)
        val user = com.karbal.tutortek.entities.User(userPostDTO)
        user.roles.add(roleEntity)
        return userRepository.save(user)
    }
}
