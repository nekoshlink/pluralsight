package org.nekosoft.shlink.sec.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@ConditionalOnWebApplication
public class ShlinkAdminApiBasicSecurityConfiguration {

    @Bean
    @Order(120)
    public SecurityFilterChain filterChainBasicAdminApi(HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests( authorize ->
                        authorize.anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic().realmName("NekoShlink")
                .and().build();
    }

}
