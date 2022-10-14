package org.nekosoft.shlink.oauth2server

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.nekosoft.shlink.oauth2server.user.NekoShlinkUserDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.OAuth2TokenType.ACCESS_TOKEN
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ClientSettings
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import java.util.stream.Collectors


@Configuration
class AuthServerConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder? =
        BCryptPasswordEncoder()

    @Bean
    @Order(98)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http // Redirect to the login page when not authenticated from the authorization endpoint
            .exceptionHandling { exceptions: ExceptionHandlingConfigurer<HttpSecurity> ->
                exceptions
                    .authenticationEntryPoint(
                        LoginUrlAuthenticationEntryPoint("/login")
                    )
            }
        return http.build()
    }

    @Bean
    @Order(99)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeHttpRequests { authorize ->
                authorize
                    .anyRequest().authenticated()
            } // Form login handles the redirect to the login page from the authorization server filter chain
            .formLogin(Customizer.withDefaults())
        return http.build()
    }

    @Bean
    fun registeredClientRepository(passwordEncoder: PasswordEncoder): RegisteredClientRepository? {
        val registeredClient: RegisteredClient = RegisteredClient
            .withId(UUID.randomUUID().toString())
            .clientId("nekoshlink")
            .clientSecret(passwordEncoder.encode("p@ssw0rd"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .scope(OidcScopes.OPENID)
            .scope("nekoshlink:manage")
            .clientSettings(
                ClientSettings.builder().requireAuthorizationConsent(true).build()
            )
            .redirectUri("https://oidcdebugger.com/debug")
            .redirectUri("http://auth-server:8080/login/oauth2/code/messaging-client-oidc")
            .redirectUri("http://auth-server:8080/authorized")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .build()
        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun jwtCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context ->
            if (
                (context.tokenType.value == OidcParameterNames.ID_TOKEN
                || context.tokenType == ACCESS_TOKEN)
                && context.authorizedScopes.contains("nekoshlink:manage")
            ) {
                val principal = context.getPrincipal<Authentication?>().principal
                        as NekoShlinkUserDetails
                val authorities = principal.user.roles.map { it.permission.name }
                context.claims.claim("nkshlink-roles", authorities)
            }
        }
    }

    @Bean
    fun providerSettings(): ProviderSettings {
        return ProviderSettings.builder()
            .issuer("http://auth-server:8080")
            .build()
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey: RSAKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    private fun generateRsaKey(): KeyPair {
        val keyPair: KeyPair = try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPairGenerator.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
        return keyPair
    }

}