package org.nekosoft.shlink.sec

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class ShlinkSecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @ConditionalOnNotWebApplication
    fun jwtAuthProvider(
        @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}") issuerUri: String,
        converter: JwtAuthenticationConverter,
    ): JwtAuthenticationProvider {
        val provider = JwtAuthenticationProvider(
            JwtDecoders.fromIssuerLocation(issuerUri)
        )
        provider.setJwtAuthenticationConverter(converter)
        return provider
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("nkshlink-roles")
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Bean
    fun userProvider(userService: UserDetailsService, pwdEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val userProvider = DaoAuthenticationProvider()
        userProvider.setUserDetailsService(userService)
        userProvider.setPasswordEncoder(pwdEncoder)
        return userProvider
    }

    @Bean
    @ConditionalOnNotWebApplication
    fun authManager(
        apiKeyProvider: ApiKeyAuthenticationProvider,
        oauth2Provider: JwtAuthenticationProvider,
        userProvider: DaoAuthenticationProvider,
    ): AuthenticationManager = ProviderManager(
        apiKeyProvider,
        oauth2Provider,
        userProvider,
        AnonymousAuthenticationProvider("NekoShlink")
    )

    companion object {

        @Bean
        fun roleHierarchy(): RoleHierarchy {
            val hierarchy = RoleHierarchyImpl()
            hierarchy.setHierarchy("""
            ROLE_Admin > ROLE_Editor
            ROLE_Editor > ROLE_Viewer
            ROLE_Everything > ROLE_Domains
            ROLE_Everything > ROLE_ShortUrls
            ROLE_Everything > ROLE_Tags
            ROLE_Everything > ROLE_Visits
            ROLE_Everything > ROLE_Stats
        """.trimIndent())
            return hierarchy
        }

        @Bean
        fun methodSecurityExpressionHandler(roleHierarchy: RoleHierarchy) : MethodSecurityExpressionHandler {
            val handler = DefaultMethodSecurityExpressionHandler();
            handler.setRoleHierarchy(roleHierarchy)
            return handler;
        }

        const val VERSION_STRING = "1"
    }

}