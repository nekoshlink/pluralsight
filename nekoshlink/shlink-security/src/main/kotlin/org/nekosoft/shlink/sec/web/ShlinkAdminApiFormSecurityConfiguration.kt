package org.nekosoft.shlink.sec.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
@ConditionalOnWebApplication
class ShlinkAdminApiFormSecurityConfiguration {

    @Bean
    @Order(99)
    fun filterChainBasicAdminApiLogin(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/login*",
                "/logout*",
            )
            formLogin { }
            logout {  }
        }
        return http.build()
    }

    @Bean
    @Order(120)
    fun filterChainBasicAdminApi(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {  }
            authorizeRequests {
                authorize(GET, "/api/v1/shorturls/**", hasRole("Viewer"))
                authorize(POST, "/api/v1/shorturls/**", hasRole("Editor"))
                authorize(PUT, "/api/v1/shorturls/**", hasRole("Editor"))
                authorize(DELETE, "/api/v1/shorturls/**", hasRole("Admin"))

                authorize(GET, "/api/v1/domains/**", hasRole("Admin"))
                authorize(POST, "/api/v1/domains/**", hasRole("Admin"))
                authorize(PUT, "/api/v1/domains/**", hasRole("Admin"))
                authorize(PATCH, "/api/v1/domains/**", hasRole("Admin"))
                authorize(DELETE, "/api/v1/domains/**", hasRole("Admin"))

                authorize(GET, "/api/v1/tags/**", hasRole("Viewer"))
                authorize(POST, "/api/v1/tags/**", hasRole("Editor"))
                authorize(PUT, "/api/v1/tags/**", hasRole("Editor"))
                authorize(PATCH, "/api/v1/tags/**", hasRole("Editor"))
                authorize(DELETE, "/api/v1/tags/**", hasRole("Admin"))

                authorize(GET, "/api/v1/visits/**", hasRole("Admin"))

                authorize(anyRequest, authenticated)
            }
            formLogin {  }
        }
        return http.build()
    }

}
