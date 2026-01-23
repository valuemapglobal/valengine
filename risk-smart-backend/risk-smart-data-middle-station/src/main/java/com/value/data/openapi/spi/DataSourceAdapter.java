package com.value.data.openapi.spi;

import com.alibaba.fastjson2.JSONObject;

/**
 * 数据源适配器SPI接口
 * 开源用户可以实现此接口来对接自己的数据源
 *
 * @author RiskSmart
 */
public interface DataSourceAdapter {

    /**
     * 获取适配器名称（唯一标识）
     * @return 适配器名称
     */
    String getName();

    /**
     * 获取适配器描述
     * @return 描述信息
     */
    default String getDescription() {
        return getName();
    }

    /**
     * 调用数据源接口
     *
     * @param apiName 接口名称/代号
     * @param param 请求参数
     * @param context 上下文信息（包含用户信息、部门信息等）
     * @return 接口响应数据
     * @throws Exception 调用异常
     */
    JSONObject invoke(String apiName, JSONObject param, DataSourceContext context) throws Exception;

    /**
     * 是否支持该接口
     *
     * @param apiName 接口名称
     * @return true-支持，false-不支持
     */
    default boolean supports(String apiName) {
        return true;
    }

    /**
     * 获取优先级，数值越小优先级越高
     * @return 优先级
     */
    default int getOrder() {
        return 100;
    }
}
