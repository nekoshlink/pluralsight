package org.nekosoft.shlink.sec.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication
class ShlinkOpenApiSecurityConfiguration {

    @Bean
    @Order(100) // order is important so that more specific matches come before more general ones
    fun filterChainOpenApi(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/{shortCode:$URL_SEGMENT_REGEX}",
                "/{shortCode:$URL_SEGMENT_REGEX}/**",
                "/qr/{shortCode:$URL_SEGMENT_REGEX}",
                "/qr/{shortCode:$URL_SEGMENT_REGEX}/**",
                "/tk/{shortCode:$URL_SEGMENT_REGEX}",
                "/tk/{shortCode:$URL_SEGMENT_REGEX}/**",
                "/robots.txt"
            )
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            formLogin {  }
        }
        return http.build()
    }

    companion object {
        const val MIN_LENGTH = 5
        const val CHAR_CLASS = "[A-Za-z0-9\\-._~]"
        const val URL_SEGMENT_REGEX = "$CHAR_CLASS{$MIN_LENGTH,}"
    }

}
