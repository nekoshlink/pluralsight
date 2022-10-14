package org.nekosoft.shlink.sec;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;
    private final String username;

    public ApiKeyAuthenticationToken(String apiKey) {
        this(apiKey, null, null);

    }

    public ApiKeyAuthenticationToken(String apiKey, String username, HashSet<GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        this.username = username;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
