package com.risksmart.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * System服务安全配置
 * 禁用CSRF并配置REST API认证
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Configuration
@EnableWebSecurity
public class SystemSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API
            .csrf(AbstractHttpConfigurer::disable)
            // Disable frame options for H2 console (if used)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/login", "/logout").permitAll()
                .requestMatchers("/captcha/**", "/captchaImage").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // All other requests require authentication (handled by token filter)
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
