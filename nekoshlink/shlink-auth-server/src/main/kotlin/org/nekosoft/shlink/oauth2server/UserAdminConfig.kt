package org.nekosoft.shlink.oauth2server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
class UserAdminConfig {

    @Bean
    @Order(1)
    fun filterChainBasicAdminApiLogin(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/adm",
                "/adm/**",
            )
            cors {  }
            csrf { disable() }
            authorizeRequests {
                authorize(anyRequest, authenticated)
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            httpBasic {  }
        }
        return http.build()
    }

}
