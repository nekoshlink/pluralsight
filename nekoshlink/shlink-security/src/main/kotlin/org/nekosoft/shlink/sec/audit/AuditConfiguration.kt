package org.nekosoft.shlink.sec.audit

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher

@Configuration
class AuditConfiguration {

    @Bean
    fun authenticateEventPublisher(pub: ApplicationEventPublisher?): AuthenticationEventPublisher {
        val authEvPub = DefaultAuthenticationEventPublisher(pub)
        authEvPub.setDefaultAuthenticationFailureEvent(GenericAuthenticationFailureEvent::class.java)
        return authEvPub
    }

}