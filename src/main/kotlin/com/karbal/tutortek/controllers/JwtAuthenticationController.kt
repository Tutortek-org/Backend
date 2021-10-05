package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.jwtDTO.JwtGetDTO
import com.karbal.tutortek.dto.jwtDTO.JwtPostDTO
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.services.JwtUserDetailsService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class JwtAuthenticationController(
    val authenticationManager: AuthenticationManager,
    val jwtTokenUtil: JwtTokenUtil,
    val userDetailsService: JwtUserDetailsService
) {

    @PostMapping(SecurityConstants.LOGIN_ENDPOINT)
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtPostDTO): JwtGetDTO {
        authenticate(authenticationRequest.username, authenticationRequest.password)
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val token = jwtTokenUtil.generateToken(userDetails)
        return JwtGetDTO(token)
    }

    @PostMapping(SecurityConstants.REGISTER_ENDPOINT)
    fun saveUser(@RequestBody userPostDTO: UserPostDTO) = userDetailsService.save(userPostDTO)

    private fun authenticate(username: String, password: String) {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        }
        catch (e: DisabledException) {
            throw Exception(ApiErrorSlug.ACCOUNT_DISABLED, e)
        }
        catch (e: BadCredentialsException) {
            throw Exception(ApiErrorSlug.INVALID_CREDENTIALS, e)
        }
    }
}
