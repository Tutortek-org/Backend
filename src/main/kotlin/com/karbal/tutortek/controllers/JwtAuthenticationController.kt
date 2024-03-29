package com.karbal.tutortek.controllers

import com.karbal.tutortek.constants.ApiErrorSlug
import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.dto.jwtDTO.JwtGetDTO
import com.karbal.tutortek.dto.jwtDTO.JwtPostDTO
import com.karbal.tutortek.dto.roleDTO.RolePostDTO
import com.karbal.tutortek.dto.userDTO.UserGetDTO
import com.karbal.tutortek.dto.userDTO.UserPostDTO
import com.karbal.tutortek.dto.userDTO.UserPutDTO
import com.karbal.tutortek.entities.RoleEntity
import com.karbal.tutortek.entities.User
import com.karbal.tutortek.security.JwtTokenUtil
import com.karbal.tutortek.security.Role
import com.karbal.tutortek.services.JwtUserDetailsService
import com.karbal.tutortek.services.RoleService
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest

@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val userService: UserService,
    private val roleService: RoleService
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
        verifyPostDto(userPostDTO)

        val userCount = userService.getUserCountByEmail(userPostDTO.email)
        if(userCount > 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.USER_ALREADY_EXISTS)

        val roleId = userPostDTO.role.ordinal + 1L
        val role = roleService.getRole(roleId)
        if(role.isEmpty)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.ROLE_NOT_FOUND)

        return UserGetDTO(userDetailsService.save(userPostDTO, role.get()))
    }

    @PostMapping("autologin")
    fun autoLogin(){}

    @GetMapping(SecurityConstants.REFRESH_ENDPOINT)
    fun refreshToken(request: HttpServletRequest): JwtGetDTO {
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val expectedMap = getMapFromIoJwtClaims(claims)
        val token = jwtTokenUtil.doGenerateRefreshToken(expectedMap, expectedMap["sub"].toString())
        return JwtGetDTO(token)
    }

    @PutMapping("assign")
    fun addRole(@RequestBody rolePostDTO: RolePostDTO): UserGetDTO {
        val roleFromDatabase = roleService.getRole(rolePostDTO.role + 1L)
        if(roleFromDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.ROLE_NOT_FOUND)
        val role = roleFromDatabase.get()
        verifyAdminRoleGrant(role)

        val userFromDatabase = userService.getUserById(rolePostDTO.userId)
        if(userFromDatabase.isEmpty)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.USER_NOT_FOUND)
        val user = userFromDatabase.get()

        user.roles.add(role)
        role.users.add(user)

        roleService.saveRole(role)
        return UserGetDTO(userService.saveUser(user))
    }

    @DeleteMapping("delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(request: HttpServletRequest) {
        val user = getUserFromDatabase(request)
        user?.id?.let { roleService.deleteRelatedRoles(it) }
        user?.id?.let { userService.deleteUserById(it) }
    }

    @PutMapping("users/{id}/ban")
    @Secured(Role.ADMIN_ANNOTATION)
    fun banUser(@PathVariable id: Long): UserGetDTO {
        val user = userService.getUserById(id)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userFromDatabase = user.get()
        userFromDatabase.isBanned = true
        return UserGetDTO(userService.saveUser(userFromDatabase))
    }

    @PutMapping("users/{id}/unban")
    @Secured(Role.ADMIN_ANNOTATION)
    fun unbanUser(@PathVariable id: Long): UserGetDTO {
        val user = userService.getUserById(id)
        if(user.isEmpty)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ApiErrorSlug.USER_NOT_FOUND)
        val userFromDatabase = user.get()
        userFromDatabase.isBanned = false
        return UserGetDTO(userService.saveUser(userFromDatabase))
    }

    @GetMapping("users")
    @Secured(Role.ADMIN_ANNOTATION)
    fun getAllUsers() = userService.getAllUsers().map { u -> UserGetDTO(u) }

    @PutMapping("password")
    fun changePassword(@RequestBody userPutDTO: UserPutDTO, request: HttpServletRequest): UserGetDTO? {
        verifyPutDto(userPutDTO)
        val user = getUserFromDatabase(request)
        return user?.let { userDetailsService.update(it, userPutDTO) }?.let { UserGetDTO(it) }
    }

    private fun getUserFromDatabase(request: HttpServletRequest): User? {
        var claims = request.getAttribute(SecurityConstants.CLAIMS_ATTRIBUTE) as DefaultClaims?
        if(claims == null) claims = jwtTokenUtil.parseClaimsFromRequest(request)
        val email = claims?.get("sub")?.toString()
        return email?.let { userService.getUserByEmail(it) }
    }

    private fun verifyAdminRoleGrant(roleEntity: RoleEntity) {
        if(roleEntity.id == Role.ADMIN.ordinal + 1L) {
            val authorities = SecurityContextHolder.getContext().authentication.authorities
            if(!authorities.contains(SimpleGrantedAuthority(Role.ADMIN_ANNOTATION)))
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, ApiErrorSlug.ADMIN_GRANT_NOT_ALLOWED)
        }
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

    private fun verifyPutDto(userPutDTO: UserPutDTO) {
        if(userPutDTO.password.length < 8)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ApiErrorSlug.PASSWORD_TOO_SHORT)
    }

    private fun verifyPostDto(userPostDTO: UserPostDTO) {
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
