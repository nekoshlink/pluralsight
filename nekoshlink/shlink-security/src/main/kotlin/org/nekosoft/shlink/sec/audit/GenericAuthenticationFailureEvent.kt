package org.nekosoft.shlink.sec.audit

import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

class GenericAuthenticationFailureEvent(
    auth: Authentication,
    authEx: AuthenticationException,
) : AbstractAuthenticationFailureEvent(auth, authEx)
