package com.risksmart.common.security.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.RequestInterceptor;

/**
 * Feign 配置注册
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Configuration
public class FeignAutoConfiguration
{
    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return new FeignRequestInterceptor();
    }
}
