package com.bydlak.bydlakapi.commons.security

import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class SecurityService {
    fun getBearerToken(request: HttpServletRequest): String? {
        val authorization = request.getHeader("Authorization")
        return if (authorization.isNotEmpty() && authorization.startsWith("Bearer "))
            authorization.substring(7, authorization.length)
        else null
    }
}