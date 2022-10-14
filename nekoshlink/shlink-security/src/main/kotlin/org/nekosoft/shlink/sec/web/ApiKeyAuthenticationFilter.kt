package org.nekosoft.shlink.sec.web

import org.nekosoft.shlink.sec.ApiKeyAuthenticationToken
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ApiKeyAuthenticationFilter : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {

        val xApiKey = request.getHeader("X-Api-Key")
            ?: return response.sendError(HttpStatus.UNAUTHORIZED.value(), "No Token Provided")

        val authentication = ApiKeyAuthenticationToken(xApiKey)

        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

}
