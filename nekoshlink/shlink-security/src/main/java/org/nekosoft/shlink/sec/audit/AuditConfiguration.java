package org.nekosoft.shlink.sec.audit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;

@Configuration
public class AuditConfiguration {

    @Bean
    public AuthenticationEventPublisher authenticateEventPublisher(ApplicationEventPublisher pub) {
        DefaultAuthenticationEventPublisher authEvPub = new DefaultAuthenticationEventPublisher(pub);
        authEvPub.setDefaultAuthenticationFailureEvent(GenericAuthenticationFailureEvent.class);
        return authEvPub;
    }

}