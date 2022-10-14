package org.nekosoft.shlink.sec.cli;

import org.nekosoft.shlink.sec.ApiKeyAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import picocli.CommandLine.*;

import java.util.HashSet;

@Component
public class AuthenticationExecutionStrategy implements IExecutionStrategy {

    private AuthenticationManager authManager;

    public AuthenticationExecutionStrategy(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public int execute(ParseResult parseResult) throws ExecutionException, ParameterException {
        String username = parseResult.matchedOption("--usr") == null
                ? null : parseResult.matchedOption("--usr").getValue();
        String password = parseResult.matchedOption("--pwd") == null
                ? null : parseResult.matchedOption("--pwd").getValue();
        String apiKey = parseResult.matchedOption("--api-key") == null
                ? null : parseResult.matchedOption("--api-key").getValue();
        Authentication authentication;
        if ((username != null && !username.isBlank()) && (password != null && !password.isBlank())) {
            authentication = new UsernamePasswordAuthenticationToken(username, password);
        } else if (username != null && !username.isBlank()) {
            authentication = new ApiKeyAuthenticationToken(apiKey);
        } else {
            HashSet<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_Anyone"));
            authentication = new AnonymousAuthenticationToken("NekoShlink", "anonymous", authorities);
        }
        try {
            Authentication authenticated = authManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            return 0;
        } catch (AuthenticationException e) {
            parseResult.commandSpec().commandLine().getErr().println(e.getMessage());
            return -3;
        }
    }

}