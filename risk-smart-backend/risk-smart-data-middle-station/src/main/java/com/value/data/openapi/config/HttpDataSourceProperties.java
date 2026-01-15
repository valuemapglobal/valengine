package com.value.data.openapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * HTTP数据源适配器配置属性
 *
 * @author RiskSmart
 */
@Data
@Component
@ConfigurationProperties(prefix = "openapi.http")
public class HttpDataSourceProperties {
    /**
     * 是否启用
     */
    private Boolean enabled = false;

    /**
     * 基础URL
     */
    private String baseUrl;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 30000;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 接口映射：apiName -> path
     */
    private Map<String, String> apiMappings;
}
