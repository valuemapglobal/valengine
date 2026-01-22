package com.risksmart.system;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 系统服务启动类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@SpringBootApplication(exclude = {PageHelperAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.risksmart")
@ComponentScan(basePackages = {"com.risksmart"})
@MapperScan({"com.risksmart.system.mapper", "com.risksmart.auth.mapper"})
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
