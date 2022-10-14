package org.nekosoft.shlink.sec.audit

import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain


@Configuration
class AuditConfiguration {

    @Bean
    fun authenticateEventPublisher(pub: ApplicationEventPublisher?): AuthenticationEventPublisher {
        val authEvPub = DefaultAuthenticationEventPublisher(pub)
        authEvPub.setDefaultAuthenticationFailureEvent(GenericAuthenticationFailureEvent::class.java)
        return authEvPub
    }

    @Bean
    fun auditEventRepository(): AuditEventRepository =
        InMemoryAuditEventRepository()

}