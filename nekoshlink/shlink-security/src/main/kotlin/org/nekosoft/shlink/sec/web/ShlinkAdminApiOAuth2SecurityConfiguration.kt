package org.nekosoft.shlink.sec.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.NullRequestCache


//@Configuration
@ConditionalOnWebApplication
class ShlinkAdminApiOAuth2SecurityConfiguration {

    @Autowired
    private lateinit var converter: JwtAuthenticationConverter

    @Bean
    @Order(120) // order is important so that more specific matches come before more general ones
    @ConditionalOnWebApplication
    fun filterChainOAuth2AdminApi(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {  }
            csrf {
                disable()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            requestCache {
                requestCache = NullRequestCache()
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = converter
                }
            }
        }
        return http.build()
    }

}