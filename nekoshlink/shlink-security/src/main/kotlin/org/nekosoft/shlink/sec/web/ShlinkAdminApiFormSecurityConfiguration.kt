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
            formLogin {  }
        }
        return http.build()
    }

}
