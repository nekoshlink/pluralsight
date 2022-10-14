package org.nekosoft.shlink.sec;

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
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.HashSet;

@Configuration
public class ShlinkSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        HashSet<GrantedAuthority> adminAuthority = new HashSet<>();
        adminAuthority.add(new SimpleGrantedAuthority("ROLE_Admin"));
        adminAuthority.add(new SimpleGrantedAuthority("ROLE_User"));
        adminAuthority.add(new SimpleGrantedAuthority("ROLE_Anyone"));
        HashSet<GrantedAuthority> userAuthority = new HashSet<>();
        userAuthority.add(new SimpleGrantedAuthority("ROLE_User"));
        userAuthority.add(new SimpleGrantedAuthority("ROLE_Anyone"));
        HashSet<GrantedAuthority> guestAuthority = new HashSet<>();
        guestAuthority.add(new SimpleGrantedAuthority("ROLE_Anyone"));
        return new InMemoryUserDetailsManager(
            new User("admin", "{noop}password1", adminAuthority),
            new User("user", "{noop}password2", userAuthority),
            new User("guest", "{noop}password3", guestAuthority)
        );
    }

    @Bean
    public DaoAuthenticationProvider userProvider(UserDetailsService detailsService) {
        DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider();
        userProvider.setUserDetailsService(detailsService);
        return userProvider;
    }

    @Bean
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