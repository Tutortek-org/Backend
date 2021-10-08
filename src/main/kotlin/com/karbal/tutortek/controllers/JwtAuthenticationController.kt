package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.jwtDTO.JwtGetDTO
import com.karbal.tutortek.dto.jwtDTO.JwtPostDTO
import com.karbal.tutortek.dto.userDTO.UserGetDTO
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.services.JwtUserDetailsService
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

@RestController
@CrossOrigin
class JwtAuthenticationController(
    val authenticationManager: AuthenticationManager,
    val jwtTokenUtil: JwtTokenUtil,
    val userDetailsService: JwtUserDetailsService,
    val userService: UserService
) {

    @PostMapping(SecurityConstants.LOGIN_ENDPOINT)
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtPostDTO): JwtGetDTO {
        authenticate(authenticationRequest.email, authenticationRequest.password)
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email)
        val token = jwtTokenUtil.generateToken(userDetails)
        return JwtGetDTO(token)
    }

    @PostMapping(SecurityConstants.REGISTER_ENDPOINT)
    @ResponseStatus(HttpStatus.CREATED)
    fun saveUser(@RequestBody userPostDTO: UserPostDTO): UserGetDTO {
        verifyDto(userPostDTO)

        val userCount = userService.getUserCountByEmail(userPostDTO.email)
        if(userCount > 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.USER_ALREADY_EXISTS)

        return UserGetDTO(userDetailsService.save(userPostDTO))
    }

    @GetMapping(SecurityConstants.REFRESH_ENDPOINT)
    fun refreshToken(request: HttpServletRequest): JwtGetDTO {
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = parseClaimsFromHeader(request)
        val expectedMap = getMapFromIoJwtClaims(claims)
        val token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap["sub"].toString())
        return JwtGetDTO(token)
    }

    private fun parseClaimsFromHeader(request: HttpServletRequest): DefaultClaims? {
        val requestTokenHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
        if(requestTokenHeader != null && requestTokenHeader.startsWith(SecurityConstants.TOKEN_BEGINNING)) {
            val jwtToken = requestTokenHeader.substring(SecurityConstants.TOKEN_BEGINNING.length)
            return jwtTokenUtil.getAllClaimsFromToken(jwtToken) as DefaultClaims?
        }
        return null
    }

    private fun getMapFromIoJwtClaims(claims: DefaultClaims?): HashMap<String, Any> {
        val expectedMap = hashMapOf<String, Any>()
        claims?.forEach { key, value -> expectedMap[key] = value }
        return expectedMap
    }

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

    private fun verifyDto(userPostDTO: UserPostDTO) {
        if(!validateEmail(userPostDTO.email))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.EMAIL_NOT_VALID)

        if(userPostDTO.password.length < 8)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.PASSWORD_TOO_SHORT)
    }

    private fun validateEmail(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}
