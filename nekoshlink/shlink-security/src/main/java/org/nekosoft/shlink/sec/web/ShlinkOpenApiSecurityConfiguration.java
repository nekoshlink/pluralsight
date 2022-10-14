package org.nekosoft.shlink.sec.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@ConditionalOnWebApplication
public class ShlinkOpenApiSecurityConfiguration {

    static final int MIN_LENGTH = 5;
    static final String CHAR_CLASS = "[A-Za-z0-9\\-._~]";
    static final String URL_SEGMENT_REGEX = String.format("%s{%d,}", CHAR_CLASS, MIN_LENGTH);

    @Bean
    @Order(100) // order is important so that more specific matches come before more general ones
    public SecurityFilterChain filterChainOpenApi(HttpSecurity http) throws Exception {
        return http.requestMatchers()
                .mvcMatchers(
                        String.format("/{shortCode:%s}", URL_SEGMENT_REGEX),
                        String.format("/{shortCode:%s}/**", URL_SEGMENT_REGEX),
                        String.format("/qr/{shortCode:%s}", URL_SEGMENT_REGEX),
                        String.format("/qr/{shortCode:%s}/**", URL_SEGMENT_REGEX),
                        String.format("/tk/{shortCode:%s}", URL_SEGMENT_REGEX),
                        String.format("/tk/{shortCode:%s}/**", URL_SEGMENT_REGEX),
                        "/robots.txt"
                )
                .and()
                .authorizeRequests( authorize ->
                        authorize.anyRequest().permitAll()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin()
                .and().build();
    }

}
