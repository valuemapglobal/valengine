package com.value.data.openapi.adapter;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.risksmart.common.core.exception.ServiceException;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.openapi.spi.DataSourceAdapter;
import com.value.data.openapi.spi.DataSourceContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * HTTP数据源适配器
 *
 * 通用的HTTP接口对接适配器，支持配置化的方式对接第三方HTTP接口。
 * 适用于对接标准RESTful API。
 *
 * 配置示例（application.yml或nacos）：
 * <pre>
 * openapi:
 *   http:
 *     enabled: true
 *     base-url: https://api.example.com
 *     timeout: 30000
 *     headers:
 *       Authorization: Bearer your-token
 *       Content-Type: application/json
 *     api-mappings:
 *       getCompanyInfo: /company/info
 *       getPersonInfo: /person/info
 * </pre>
 *
 * @author RiskSmart
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "openapi.http.enabled", havingValue = "true", matchIfMissing = false)
public class HttpDataSourceAdapter implements DataSourceAdapter {

    private final HttpDataSourceProperties properties;

    public HttpDataSourceAdapter(HttpDataSourceProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getName() {
        return "http";
    }

    @Override
    public String getDescription() {
        return "HTTP数据源适配器";
    }

    @Override
    public int getOrder() {
        return 50;
    }

    @Override
    public boolean supports(String apiName) {
        if (properties.getApiMappings() == null) {
            return false;
        }
        return properties.getApiMappings().containsKey(apiName);
    }

    @Override
    public JSONObject invoke(String apiName, JSONObject param, DataSourceContext context) throws Exception {
        String path = properties.getApiMappings().get(apiName);
        if (StringUtils.isBlank(path)) {
            throw new ServiceException("未配置接口映射: " + apiName);
        }

        String url = properties.getBaseUrl() + path;
        log.info("HTTP适配器调用 - URL: {}, 订单: {}", url, context.getOrderId());

        // 构建请求
        HttpRequest request = HttpRequest.post(url)
                .timeout(properties.getTimeout() != null ? properties.getTimeout() : 30000)
                .body(param.toJSONString());

        // 添加请求头
        if (properties.getHeaders() != null) {
            properties.getHeaders().forEach(request::header);
        }

        // 添加追踪头
        request.header("X-Request-Id", context.getOrderId());
        if (context.getUserId() != null) {
            request.header("X-User-Id", context.getUserId().toString());
        }

        // 执行请求
        try (HttpResponse response = request.execute()) {
            String body = response.body();
            log.debug("HTTP适配器响应 - Status: {}, Body: {}", response.getStatus(), body);

            if (!response.isOk()) {
                throw new ServiceException("HTTP请求失败: " + response.getStatus());
            }

            if (!JSONUtil.isTypeJSON(body)) {
                throw new ServiceException("响应格式错误，非JSON格式");
            }

            return JSONObject.parseObject(body);
        }
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "openapi.http")
    public static class HttpDataSourceProperties {
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
}
