package com.inno.apigateway_service.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtProvider jwtTokenProvider;

    public WebSecurityConfig(JwtProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        http.authorizeExchange(exchange -> exchange.anyExchange().permitAll());

        return http.build();
    }
}

