package org.nekosoft.shlink.sec

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ApiKeyAuthenticationToken(
    private val apiKey: String,
    private val username: String? = null,
    authorities: HashSet<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): String = apiKey

    override fun getPrincipal(): String? = username

}
