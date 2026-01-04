package com.value.data.config;

import com.value.data.common.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Vida
 * @date 2023年08月08日 11:11
 * @description
 */
@Configuration
public class SnowflakeConfig {
    @Value("${snowFlake.datacenterId}")
    private Long datacenterId;
    @Value("${snowFlake.machineId}")
    private Long machineId;


    @Bean
    public SnowFlake getSnowFlake(){
        SnowFlake snowFlake = new SnowFlake(datacenterId,machineId);
        return snowFlake;
    }
}
