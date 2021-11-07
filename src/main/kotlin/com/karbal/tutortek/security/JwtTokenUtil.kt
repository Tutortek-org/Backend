package com.karbal.tutortek.security

import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.services.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*
import java.util.function.Function
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

@Component
class JwtTokenUtil(val userService: UserService) : Serializable {

    @Value("\${jwt.secret}")
    private val secret: String = ""

    fun getUsernameFromToken(token: String): String = getClaimFromToken(token, Claims::getSubject)

    fun getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getExpiration)

    private fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    fun getAllClaimsFromToken(token: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = hashMapOf<String, Any>()
        val user = userService.getUserByEmail(userDetails.username)
        user.id?.let { claims.put("uid", it) }
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: HashMap<String, Any>, subject: String) = Jwts.builder().setClaims(claims)
        .setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
        .signWith(SignatureAlgorithm.HS512, secret).compact()

    fun doGenerateRefreshToken(claims: HashMap<String, Any>, subject: String): String = Jwts.builder().setClaims(claims)
        .setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + SecurityConstants.REFRESH_EXPIRATION))
        .signWith(SignatureAlgorithm.HS512, secret).compact()

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun parseClaimsFromRequest(request: HttpServletRequest): DefaultClaims? {
        val requestTokenHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
        if(requestTokenHeader != null && requestTokenHeader.startsWith(SecurityConstants.TOKEN_BEGINNING)) {
            val jwtToken = requestTokenHeader.substring(SecurityConstants.TOKEN_BEGINNING.length)
            return getAllClaimsFromToken(jwtToken) as DefaultClaims?
        }
        return null
    }
}
