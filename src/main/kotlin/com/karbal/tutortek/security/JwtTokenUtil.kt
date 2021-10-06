package com.karbal.tutortek.security

import com.karbal.tutortek.constants.SecurityConstants
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*
import java.util.function.Function
import kotlin.collections.HashMap

@Component
class JwtTokenUtil : Serializable {

    @Value("\${jwt.secret}")
    private val secret: String = ""

    fun getUsernameFromToken(token: String): String = getClaimFromToken(token, Claims::getSubject)

    fun getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getExpiration)

    private fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = hashMapOf<String, Any>()
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
}
