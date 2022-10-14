package org.nekosoft.shlink.sec.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
                .authorizeRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .formLogin()
                .and().build();
    }

}
