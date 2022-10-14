package org.nekosoft.shlink.sec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.HashSet;

@Configuration
public class ShlinkSecurityConfiguration {

    public static final String VERSION_STRING = "1";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider userProvider(UserDetailsService userService, PasswordEncoder pwdEncoder) {
        DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider();
        userProvider.setUserDetailsService(userService);
        userProvider.setPasswordEncoder(pwdEncoder);
        return userProvider;
    }

    @Bean
    @ConditionalOnNotWebApplication
    public AuthenticationManager authManager(
            ApiKeyAuthenticationProvider apiKeyProvider,
            DaoAuthenticationProvider userProvider
    ) {
        return new ProviderManager(
                apiKeyProvider,
                userProvider,
                new AnonymousAuthenticationProvider("NekoShlink")
        );
    }

}