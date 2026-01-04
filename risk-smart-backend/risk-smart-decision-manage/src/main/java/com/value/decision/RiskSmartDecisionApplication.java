package com.value.decision;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * RiskSmart Decision Engine Application
 *
 * @author RiskSmart Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.value.decision", "com.risksmart.system"})
@MapperScan({"com.value.decision.**.mapper", "com.risksmart.system.mapper"})
@EnableFeignClients(basePackages = {"com.value.decision", "com.risksmart.system.api"})
@EnableCaching
@EnableAsync
@EnableScheduling
public class RiskSmartDecisionApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskSmartDecisionApplication.class, args);
    }
}
