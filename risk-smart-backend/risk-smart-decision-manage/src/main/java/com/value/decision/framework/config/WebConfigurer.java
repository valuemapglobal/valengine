package com.value.decision.framework.config;

import com.value.decision.framework.resolver.CurrUserMethodArgumentResolver;
import com.value.decision.framework.resolver.LoginUserArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * web配置
 * @author Raysen
 * @create 2021/10/20 12:03
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    /**
     * 实例化一个自己注解解析器，
     * 然后add进spring的List<HandlerMethodArgumentResolver> argumentResolvers中
     * 最后交给spring统一管理
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currUserMethodArgumentResolver());
        argumentResolvers.add(loginUserArgumentResolver());
        WebMvcConfigurer.super.addArgumentResolvers(argumentResolvers);
    }

    /**
     * 实例化自己的注解解析器
     * @return
     */
    @Bean
    public CurrUserMethodArgumentResolver currUserMethodArgumentResolver() {
        return new CurrUserMethodArgumentResolver();

    }

    @Bean
    public LoginUserArgumentResolver loginUserArgumentResolver() {
        return new LoginUserArgumentResolver();
    }
}
