package com.value.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 特征变量属性配置
 *
 * @author vlauemap team
 * @since 2026/01/22
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
