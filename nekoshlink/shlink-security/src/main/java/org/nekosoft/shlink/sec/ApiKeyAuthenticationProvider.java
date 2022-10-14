package org.nekosoft.shlink.sec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value("${nekoshlink.security.api-key:}")
    private String serverApiKey = "";

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (serverApiKey.isBlank() || !(authentication instanceof ApiKeyAuthenticationToken)) {
            return null;
        }
        if (authentication.getCredentials().equals(serverApiKey)) {
            HashSet<GrantedAuthority> auths = new HashSet<>();
            auths.add(new SimpleGrantedAuthority("ROLE_API_Key_User"));
            ApiKeyAuthenticationToken authenticated = new ApiKeyAuthenticationToken(authentication.getCredentials().toString(), "api", auths);
            authenticated.setAuthenticated(true);
            return authenticated;
        } else {
            throw new BadCredentialsException("Invalid API Key");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == ApiKeyAuthenticationToken.class;
    }

}

