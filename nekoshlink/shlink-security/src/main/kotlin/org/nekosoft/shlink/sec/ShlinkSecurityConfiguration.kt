package org.nekosoft.shlink.sec

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ShlinkSecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userProvider(userService: UserDetailsService, pwdEncoder: PasswordEncoder): DaoAuthenticationProvider {
        val userProvider = DaoAuthenticationProvider()
        userProvider.setUserDetailsService(userService)
        userProvider.setPasswordEncoder(pwdEncoder)
        return userProvider
    }

    @Bean
    fun authManager(
        apiKeyProvider: ApiKeyAuthenticationProvider,
        userProvider: DaoAuthenticationProvider,
    ): AuthenticationManager = ProviderManager(
        apiKeyProvider,
        userProvider,
        AnonymousAuthenticationProvider("NekoShlink")
    )

    companion object {
        const val VERSION_STRING = "1"
    }

}