package com.bydlak.bydlakapi.commons.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilter(
    val securityService: SecurityService,
) : OncePerRequestFilter() {

    @Override
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = securityService.getBearerToken(request) ?: return
        val decoded = FirebaseAuth.getInstance().verifyIdToken(token)
        decoded?.let {
            val user = toUser(decoded)
            val authority = SimpleGrantedAuthority("ROLE_USER")
            val authentication = UsernamePasswordAuthenticationToken(user, null, listOf(authority))
            SecurityContextHolder.getContext().setAuthentication(authentication)
            filterChain.doFilter(request, response)
        }
    }
}

fun toUser(token: FirebaseToken) = User(token.uid, token.email)