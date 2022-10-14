package org.nekosoft.shlink.sec.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
@ConditionalOnWebApplication
public class ShlinkAdminApiX509SecurityConfiguration {

    @Autowired
    private UserDetailsService users;

    @Bean
    @Order(120) // order is important so that more specific matches come before more general ones
    public SecurityFilterChain filterChainX509AdminApi(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .x509().userDetailsService(users)
                .and().build();
    }

}