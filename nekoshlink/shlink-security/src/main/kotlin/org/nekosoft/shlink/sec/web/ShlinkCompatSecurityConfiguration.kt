package org.nekosoft.shlink.sec.web

import org.nekosoft.shlink.sec.ApiKeyAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.security.web.savedrequest.NullRequestCache

@Configuration
@ConditionalOnWebApplication
class ShlinkCompatSecurityConfiguration {

    @Autowired
    private lateinit var apikeyAuthProvider: ApiKeyAuthenticationProvider

    @Bean
    @Order(110) // order is important so that more specific matches come before more general ones
    fun filterChainShlinkCompat(http: HttpSecurity): SecurityFilterChain {
        http {
            securityMatcher(
                "/rest/v2",
                "/rest/v2/**",
            )
            cors {  }
            csrf { disable() }
            authorizeRequests {
                authorize(anyRequest, authenticated)
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authenticationManager = ProviderManager(apikeyAuthProvider)
            requestCache {
                requestCache = NullRequestCache()
            }
            addFilterBefore<AnonymousAuthenticationFilter>(ApiKeyAuthenticationFilter())
        }
        return http.build()
    }

}
