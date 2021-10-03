package com.karbal.tutortek.services

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        if(username == "javainuse")
            return User("javainuse", "\$2a\$10\$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", arrayListOf())
        else throw UsernameNotFoundException("User not found with username: $username")
    }
}
