package com.karbal.tutortek.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import java.util.*
import java.util.function.Function
import kotlin.collections.HashMap

class JwtTokenUtil : Serializable {

    private val serialVersionUID = -2550185165626007488L
    private val JWT_TOKEN_VALIDITY = 18000

    @Value("\${jwt.secret}")
    private val secret: String = ""

    fun getUsernameFromToken(token: String) = getClaimFromToken(token, Claims::getSubject)

    fun getExpirationDateFromToken(token: String) = getClaimFromToken(token, Claims::getExpiration)

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
        .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
        .signWith(SignatureAlgorithm.HS512, secret).compact()

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}
