package org.nekosoft.shlink.sec.cli

import org.nekosoft.shlink.sec.ApiKeyAuthenticationToken
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component
import picocli.CommandLine.*
import java.io.File

@Component
@ConditionalOnNotWebApplication
class AuthenticationExecutionStrategy(
    private val authManager: AuthenticationManager
) : IExecutionStrategy {
    override fun execute(parseResult: ParseResult): Int {
        val username = parseResult.matchedOption("--usr")?.getValue<String>()
        val password = parseResult.matchedOption("--pwd")?.getValue<String>()
        val apiKey = parseResult.matchedOption("--api-key")?.getValue<String>()
        val accessTokenFile = parseResult.matchedOption("--access-token-file")?.getValue<File>()
        val authentication = if (accessTokenFile != null && accessTokenFile.canRead()) {
            BearerTokenAuthenticationToken(accessTokenFile.readText())
        } else if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            UsernamePasswordAuthenticationToken(username, password)
        } else if (!apiKey.isNullOrBlank()) {
            ApiKeyAuthenticationToken(apiKey)
        } else {
            val authorities = HashSet<GrantedAuthority>()
            authorities.add(SimpleGrantedAuthority("ROLE_Anyone"))
            AnonymousAuthenticationToken("NekoShlink", "anonymous", authorities)
        }
        return try {
            val authenticated = authManager.authenticate(authentication)
            SecurityContextHolder.getContext().authentication = authenticated
            0
        } catch (e: AuthenticationException) {
            parseResult.commandSpec().commandLine().err.println(e.message)
            -3
        }
    }
}