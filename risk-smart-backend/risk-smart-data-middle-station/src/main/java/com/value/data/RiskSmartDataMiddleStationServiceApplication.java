package com.value.data;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {PageHelperAutoConfiguration.class})
@ComponentScan(basePackages = {"com.value.data", "com.risksmart.common.core"})
@MapperScan({"com.value.data.mapper"})
@EnableFeignClients({"com.ruoyi.system.api", "com.risksmart.system.api", "com.value.data.service.feign"})
@EnableConfigurationProperties
public class RiskSmartDataMiddleStationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskSmartDataMiddleStationServiceApplication.class, args);
    }
}
