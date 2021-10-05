package com.karbal.tutortek.security

import com.karbal.tutortek.constants.SecurityConstants
import com.karbal.tutortek.services.JwtUserDetailsService
import io.jsonwebtoken.ExpiredJwtException
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
        try {
            val requestTokenHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER)
            var username: String? = null
            var jwtToken = ""

            if(requestTokenHeader != null && requestTokenHeader.startsWith(SecurityConstants.TOKEN_BEGINNING)) {
                jwtToken = requestTokenHeader.substring(SecurityConstants.TOKEN_BEGINNING.length)
                username = jwtTokenUtil.getUsernameFromToken(jwtToken)
            }

            if(username != null && SecurityContextHolder.getContext().authentication == null)
                setAuthenticationTokenOnSecurityContext(username, jwtToken, request)
        }
        catch (e: ExpiredJwtException) {
            handleExpiredJwt(request, e)
        }
        chain.doFilter(request, response)
    }

    private fun handleExpiredJwt(request: HttpServletRequest, e: ExpiredJwtException) {
        val isRefreshToken = request.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER)
        val requestUrl = request.requestURL.toString()
        if (isRefreshToken == "true" && requestUrl.contains(SecurityConstants.REFRESH_ENDPOINT))
            allowForRefreshToken(e, request)
        else request.setAttribute("exception", e)
    }

    private fun allowForRefreshToken(exception: ExpiredJwtException, request: HttpServletRequest) {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(null, null, null)
        request.setAttribute(SecurityConstants.CLAIMS_ATTRIBUTE, exception.claims)
    }

    private fun setAuthenticationTokenOnSecurityContext(username: String?, jwtToken: String, request: HttpServletRequest) {
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)
        if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
            val usernamePasswordAuthenticationToken =
                UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
    }
}
