package org.nekosoft.shlink.sec.audit

import mu.KotlinLogging
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

private val kLogger = KotlinLogging.logger {}

@Component
class AccessLogger {

    @EventListener
    fun onSuccess(event: AuthenticationSuccessEvent) {
        kLogger.warn("Successful access attempt for {}", event.authentication)
    }

    @EventListener
    fun onFailure(event: AbstractAuthenticationFailureEvent) {
        kLogger.warn("Failed access attempt for {} due to {}", event.authentication, event.exception.message)
    }

    @EventListener
    fun on(event: AuditApplicationEvent) {
        kLogger.warn("An Audit Event was received: {}", event)
    }

}