package com.value.data.openapi.spi;

import lombok.Builder;
import lombok.Data;

/**
 * 数据源调用上下文
 *
 * @author RiskSmart
 */
@Data
@Builder
public class DataSourceContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 订单号/请求标识
     */
    private String orderId;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 扩展属性
     */
    private java.util.Map<String, Object> extras;

    /**
     * 获取扩展属性
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return (T) extras.get(key);
    }

    /**
     * 设置扩展属性
     */
    public void setExtra(String key, Object value) {
        if (extras == null) {
            extras = new java.util.HashMap<>();
        }
        extras.put(key, value);
    }
}
