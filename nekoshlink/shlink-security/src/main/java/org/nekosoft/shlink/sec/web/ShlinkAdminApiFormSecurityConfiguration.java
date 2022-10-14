package org.nekosoft.shlink.sec.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;

//@Configuration
@ConditionalOnWebApplication
public class ShlinkAdminApiFormSecurityConfiguration {

    @Bean
    @Order(99)
    public SecurityFilterChain filterChainBasicAdminApiLogin(HttpSecurity http) throws Exception {
        return http
                .requestMatchers().mvcMatchers(
                        "/login*",
                        "/logout*"
                )
                .and()
                .authorizeRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .logout()
                .and().build();
    }

    @Bean
    @Order(120)
    public SecurityFilterChain filterChainBasicAdminApi(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .authorizeRequests()

                .mvcMatchers(GET, "/api/v1/shorturls/**").hasRole("Viewer")
                .mvcMatchers(POST, "/api/v1/shorturls/**").hasRole("Editor")
                .mvcMatchers(PUT, "/api/v1/shorturls/**").hasRole("Editor")
                .mvcMatchers(DELETE, "/api/v1/shorturls/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(POST, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(PUT, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(PATCH, "/api/v1/domains/**").hasRole("Admin")
                .mvcMatchers(DELETE, "/api/v1/domains/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/tags/**").hasRole("Viewer")
                .mvcMatchers(POST, "/api/v1/tags/**").hasRole("Editor")
                .mvcMatchers(PUT, "/api/v1/tags/**").hasRole("Editor")
                .mvcMatchers(PATCH, "/api/v1/tags/**").hasRole("Editor")
                .mvcMatchers(DELETE, "/api/v1/tags/**").hasRole("Admin")

                .mvcMatchers(GET, "/api/v1/visits/**").hasRole("Admin")

                .anyRequest().authenticated()

                .and()
                .formLogin()
                .and().build();
    }

}
