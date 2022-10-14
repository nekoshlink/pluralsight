package org.nekosoft.shlink.sec.audit;

import kotlin.Unit;
import mu.KLogger;
import mu.KotlinLogging;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


@Component
class AccessLogger {

    private static KLogger kLogger = KotlinLogging.INSTANCE.logger(() -> Unit.INSTANCE);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        kLogger.warn("Successful access attempt for {}", event.getAuthentication());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        kLogger.warn("Failed access attempt for {} due to {}", event.getAuthentication(), event.getException().getMessage());
    }

}