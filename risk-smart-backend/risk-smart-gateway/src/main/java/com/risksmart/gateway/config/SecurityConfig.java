package com.risksmart.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 网关安全配置
 * 禁用CSRF以支持REST API
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // 禁用CSRF以支持REST API
            .csrf(csrf -> csrf.disable())
            // 允许所有请求通过（认证由下游服务处理）
            .authorizeExchange(exchange -> exchange
                .anyExchange().permitAll()
            );
        return http.build();
    }
}
