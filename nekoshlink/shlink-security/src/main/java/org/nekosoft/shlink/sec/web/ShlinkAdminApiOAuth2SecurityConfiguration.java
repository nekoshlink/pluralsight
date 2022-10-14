package org.nekosoft.shlink.sec.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import static org.springframework.http.HttpMethod.*;


@Configuration
@ConditionalOnWebApplication
public class ShlinkAdminApiOAuth2SecurityConfiguration {

    @Autowired
    private JwtAuthenticationConverter converter;

    @Bean
    @Order(120) // order is important so that more specific matches come before more general ones
    @ConditionalOnWebApplication
    public SecurityFilterChain filterChainOAuth2AdminApi(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()

                .mvcMatchers(GET, "/api/v1/shorturls/**").hasAnyRole("Admin", "Editor", "Viewer")
                .mvcMatchers(POST, "/api/v1/shorturls/**").hasAnyRole("Admin", "Editor")
                .mvcMatchers(PUT, "/api/v1/shorturls/**").hasAnyRole("Admin", "Editor")
                .mvcMatchers(DELETE, "/api/v1/shorturls/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(POST, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(PUT, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(PATCH, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(DELETE, "/api/v1/domains/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/tags/**").hasAnyRole("Admin", "Editor", "Viewer")
                .mvcMatchers(POST, "/api/v1/tags/**").hasAnyRole("Admin", "Editor")
                .mvcMatchers(PUT, "/api/v1/tags/**").hasAnyRole("Admin", "Editor")
                .mvcMatchers(PATCH, "/api/v1/tags/**").hasAnyRole("Admin", "Editor")
                .mvcMatchers(DELETE, "/api/v1/tags/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/visits/**").hasRole("Admin")

                .anyRequest().authenticated()

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestCache().requestCache(new NullRequestCache())
                .and()
                .oauth2ResourceServer().jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(converter)
                )
                .and().build();

    }

}