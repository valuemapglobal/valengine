package com.value.data.service;


import com.value.data.domain.entity.FeatureModuleEntity;

import java.util.Map;

/**
 * 特征变量服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface FeatureVariableService {
    Map<String,String> getScripts(FeatureModuleEntity featureModuleEntity);

    Object entryPoint(Map<String,Object> params, String name);
}
