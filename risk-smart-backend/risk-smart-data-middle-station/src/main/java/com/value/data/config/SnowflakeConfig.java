package com.value.data.config;

import com.risksmart.common.core.utils.uuid.SnowFlake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法配置类
 *
 * @author vlauemap team
 * @since 2026/01/22
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
