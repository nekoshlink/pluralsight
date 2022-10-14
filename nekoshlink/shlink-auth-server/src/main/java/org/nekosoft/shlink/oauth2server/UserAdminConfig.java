package org.nekosoft.shlink.oauth2server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class UserAdminConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainBasicAdminApiLogin(HttpSecurity http) throws Exception {
        return http
                .requestMatchers().mvcMatchers(
                        "/adm",
                        "/adm/**"
                )
                .and()
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
                .and().build();
    }

}
