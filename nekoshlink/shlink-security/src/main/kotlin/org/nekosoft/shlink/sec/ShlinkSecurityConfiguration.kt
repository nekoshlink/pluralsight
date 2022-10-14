package org.nekosoft.shlink.sec

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager

@Configuration
class ShlinkSecurityConfiguration {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val adminAuthority = HashSet<GrantedAuthority>()
        adminAuthority.add(SimpleGrantedAuthority("ROLE_Admin"))
        adminAuthority.add(SimpleGrantedAuthority("ROLE_User"))
        adminAuthority.add(SimpleGrantedAuthority("ROLE_Anyone"))
        val userAuthority = HashSet<GrantedAuthority>()
        userAuthority.add(SimpleGrantedAuthority("ROLE_User"))
        userAuthority.add(SimpleGrantedAuthority("ROLE_Anyone"))
        val guestAuthority = HashSet<GrantedAuthority>()
        guestAuthority.add(SimpleGrantedAuthority("ROLE_Anyone"))
        return InMemoryUserDetailsManager(
            User("admin", "{noop}password1", adminAuthority),
            User("user", "{noop}password2", userAuthority),
            User("guest", "{noop}password3", guestAuthority),
        )
    }

    @Bean
    fun userProvider(detailsService: UserDetailsService): DaoAuthenticationProvider {
        val userProvider = DaoAuthenticationProvider()
        userProvider.setUserDetailsService(detailsService)
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

}