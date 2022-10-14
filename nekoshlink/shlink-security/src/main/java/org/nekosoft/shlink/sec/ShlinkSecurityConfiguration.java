package org.nekosoft.shlink.sec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class ShlinkSecurityConfiguration {

    public static final String VERSION_STRING = "1";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnNotWebApplication
    public JwtAuthenticationProvider jwtAuthProvider(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            JwtAuthenticationConverter converter
            ) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(
                JwtDecoders.fromIssuerLocation(issuerUri)
        );
        provider.setJwtAuthenticationConverter(converter);
        return provider;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("nkshlink-roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
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
            JwtAuthenticationProvider oauth2Provider,
            DaoAuthenticationProvider userProvider
    ) {
        return new ProviderManager(
                apiKeyProvider,
                oauth2Provider,
                userProvider,
                new AnonymousAuthenticationProvider("NekoShlink")
        );
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
            ROLE_Admin > ROLE_Editor
            ROLE_Editor > ROLE_Viewer
        """.stripIndent());
        return hierarchy;
    }


}