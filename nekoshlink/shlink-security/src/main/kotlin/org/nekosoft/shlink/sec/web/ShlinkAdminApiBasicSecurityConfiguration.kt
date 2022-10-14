package org.nekosoft.shlink.sec.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
@ConditionalOnWebApplication
class ShlinkAdminApiBasicSecurityConfiguration {

    @Bean
    @Order(120)
    fun filterChainBasicAdminApi(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {  }
            csrf {
                disable()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            httpBasic {
                realmName = "NekoShlink"
            }
        }
        return http.build()
    }

}
