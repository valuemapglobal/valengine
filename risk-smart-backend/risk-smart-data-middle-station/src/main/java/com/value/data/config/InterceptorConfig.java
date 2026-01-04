package com.value.data.config;

import com.value.data.interceptor.ApiInterceptor;
import com.value.data.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    private final ApiInterceptor apiInterceptor;
    @Autowired
    public InterceptorConfig(RateLimitInterceptor rateLimitInterceptor, ApiInterceptor apiInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
        this.apiInterceptor = apiInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
        registry.addInterceptor(apiInterceptor);
    }
}
