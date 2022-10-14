package org.nekosoft.shlink.sec.audit;

import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class GenericAuthenticationFailureEvent extends AbstractAuthenticationFailureEvent {
    public GenericAuthenticationFailureEvent(Authentication auth, AuthenticationException authEx) {
        super(auth, authEx);
    }
}
