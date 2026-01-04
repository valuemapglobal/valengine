package com.value.data.service;

import java.util.Map;

/**
 * @author Vida
 * @date 2025年04月24日 14:31
 * @description
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
