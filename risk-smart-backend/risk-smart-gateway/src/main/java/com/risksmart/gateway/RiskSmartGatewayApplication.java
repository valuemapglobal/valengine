package com.risksmart.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * RiskSmart网关应用启动类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class RiskSmartGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskSmartGatewayApplication.class, args);
    }
}