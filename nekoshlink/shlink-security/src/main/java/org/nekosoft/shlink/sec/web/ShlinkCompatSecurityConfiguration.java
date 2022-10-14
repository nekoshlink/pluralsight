package org.nekosoft.shlink.sec.web;

import org.nekosoft.shlink.sec.ApiKeyAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@ConditionalOnWebApplication
public class ShlinkCompatSecurityConfiguration {

    @Autowired
    private ApiKeyAuthenticationProvider apikeyAuthProvider;

    @Bean
    @Order(110) // order is important so that more specific matches come before more general ones
    public SecurityFilterChain filterChainShlinkCompat(HttpSecurity http) throws Exception {
        return http
                .requestMatchers(requestMatcherConfigurer ->
                        requestMatcherConfigurer.mvcMatchers(
                                "/rest/v2",
                                "/rest/v2/**"
                        )
                )
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationManager(new ProviderManager(apikeyAuthProvider))
                .requestCache().requestCache(new NullRequestCache())
                .and()
                .addFilterBefore(new ApiKeyAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .build();
    }

}
