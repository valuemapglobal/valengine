package com.value.data.openapi.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.value.data.openapi.spi.DataSourceAdapter;
import com.value.data.openapi.spi.DataSourceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 示例数据源适配器
 *
 * 这是一个示例实现，展示如何实现DataSourceAdapter接口来对接自己的数据源。
 * 开源用户可以参考此示例，实现自己的适配器。
 *
 * 使用方法：
 * 1. 实现DataSourceAdapter接口
 * 2. 添加@Component注解，让Spring自动注册
 * 3. 实现invoke方法，对接你的数据源
 *
 * 启用此示例适配器：
 * 在配置文件中添加：openapi.sample.enabled=true
 *
 * @author RiskSmart
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "openapi.sample.enabled", havingValue = "true", matchIfMissing = false)
public class SampleDataSourceAdapter implements DataSourceAdapter {

    @Override
    public String getName() {
        return "sample";
    }

    @Override
    public String getDescription() {
        return "示例数据源适配器（仅供参考）";
    }

    @Override
    public int getOrder() {
        return 999; // 最低优先级
    }

    @Override
    public boolean supports(String apiName) {
        // 示例：只支持以"sample_"开头的接口
        return apiName != null && apiName.startsWith("sample_");
    }

    @Override
    public JSONObject invoke(String apiName, JSONObject param, DataSourceContext context) throws Exception {
        log.info("示例适配器被调用 - 接口: {}, 订单: {}, 用户: {}",
                apiName, context.getOrderId(), context.getUserName());

        // 这里实现你的数据源调用逻辑
        // 例如：调用第三方API、查询数据库、调用内部服务等

        // 示例返回
        JSONObject response = new JSONObject();
        response.put("code", 0);
        response.put("msg", "success");

        JSONObject data = new JSONObject();
        data.put("apiName", apiName);
        data.put("orderId", context.getOrderId());
        data.put("timestamp", System.currentTimeMillis());
        data.put("message", "这是示例适配器的返回数据，请实现自己的适配器");
        data.put("inputParam", param);

        response.put("data", data);
        return response;
    }
}
