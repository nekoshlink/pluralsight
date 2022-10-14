package org.nekosoft.shlink.sec.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
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
                authorize(HttpMethod.GET, "/api/v1/shorturls/**", hasAnyRole("Admin", "Editor", "Viewer"))
                authorize(HttpMethod.POST, "/api/v1/shorturls/**", hasAnyRole("Admin", "Editor"))
                authorize(HttpMethod.PUT, "/api/v1/shorturls/**", hasAnyRole("Admin", "Editor"))
                authorize(HttpMethod.DELETE, "/api/v1/shorturls/**", hasRole("Admin"))

                authorize(HttpMethod.GET, "/api/v1/domains/**", hasRole("Admin"))
                authorize(HttpMethod.POST, "/api/v1/domains/**", hasRole("Admin"))
                authorize(HttpMethod.PUT, "/api/v1/domains/**", hasRole("Admin"))
                authorize(HttpMethod.PATCH, "/api/v1/domains/**", hasRole("Admin"))
                authorize(HttpMethod.DELETE, "/api/v1/domains/**", hasRole("Admin"))

                authorize(HttpMethod.GET, "/api/v1/tags/**", hasAnyRole("Admin", "Editor", "Viewer"))
                authorize(HttpMethod.POST, "/api/v1/tags/**", hasAnyRole("Admin", "Editor"))
                authorize(HttpMethod.PUT, "/api/v1/tags/**", hasAnyRole("Admin", "Editor"))
                authorize(HttpMethod.PATCH, "/api/v1/tags/**", hasAnyRole("Admin", "Editor"))
                authorize(HttpMethod.DELETE, "/api/v1/tags/**", hasRole("Admin"))

                authorize(HttpMethod.GET, "/api/v1/visits/**", hasRole("Admin"))

                authorize(anyRequest, authenticated)
            }
            formLogin {  }
        }
        return http.build()
    }

}
