package com.value.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Vida
 * @date 2025年04月03日 15:21
 * @description
 */
@Component
@Data
@ConfigurationProperties(prefix = "feature")
public class FeatureProperties {
    /**
     * 数据中台url
     */
    private String url = "http://127.0.0.1:8988/interfaceRequest/api";

    /**
     * 沙箱环境配置
     */
    private SandBox sandBox = new SandBox();

    @Data
    public static class SandBox {
        private Long maxCPUTime = 10000L;
        private Long maxMemory = 512*1024 * 1024L;
        private Boolean allowNoBraces = false;
        private Boolean allowLoadFunctions = true;
        private Integer maxPreparedStatements = 0;
    }
}
