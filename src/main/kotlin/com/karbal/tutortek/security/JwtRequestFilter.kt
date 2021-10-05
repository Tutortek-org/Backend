package com.karbal.tutortek.security

import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.services.JwtUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource

@Component
class JwtRequestFilter(
    val jwtUserDetailsService: JwtUserDetailsService,
    val jwtTokenUtil: JwtTokenUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val requestTokenHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)

        var username: String? = null
        var jwtToken = ""

        if(requestTokenHeader != null && requestTokenHeader.startsWith(SecurityConstants.TOKEN_BEGINNING)) {
            jwtToken = requestTokenHeader.substring(SecurityConstants.TOKEN_BEGINNING.length)
            username = jwtTokenUtil.getUsernameFromToken(jwtToken)
        }

        if(username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = jwtUserDetailsService.loadUserByUsername(username)

            if(jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
            }
        }
        chain.doFilter(request, response)
    }
}
