package com.value.decision.framework.config;

import com.value.decision.framework.resolver.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web 配置
 * @author Raysen
 * @create 2021/10/20 12:03
 */
@Configuration
@RequiredArgsConstructor
public class WebConfigurer implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }
}
