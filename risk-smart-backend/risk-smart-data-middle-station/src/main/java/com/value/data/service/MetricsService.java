package com.value.data.service;

import java.util.Map;

/**
 * 分析指标服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface MetricsService {
    /**
     * 入口方法
     * @param params
     * @param name
     * @return
     */
    Object entryPoint(Map<String,Object> params, String name);
}
