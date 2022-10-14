package org.nekosoft.shlink.sec

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class ApiKeyAuthenticationProvider : AuthenticationProvider {

    @Value("\${nekoshlink.security.api-key:}")
    private lateinit var serverApiKey: String

    override fun authenticate(authentication: Authentication): Authentication? {
        if (serverApiKey.isBlank() || authentication !is ApiKeyAuthenticationToken) {
            return null
        }
        if (authentication.credentials == serverApiKey) {
            val auths = HashSet<GrantedAuthority>()
            auths.add(SimpleGrantedAuthority("ROLE_API_Key_User"))
            val authenticated = ApiKeyAuthenticationToken(authentication.credentials, "api", auths)
            authenticated.isAuthenticated = true
            return authenticated
        } else {
            throw BadCredentialsException("Invalid API Key")
        }
    }

    override fun supports(authentication: Class<*>) =
        authentication == ApiKeyAuthenticationToken::class.java

}
