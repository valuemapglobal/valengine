package com.value.data.service;


import com.value.data.domain.entity.FeatureModuleEntity;

import java.util.Map;

/**
 * @author Vida
 * @date 2025年04月03日 15:01
 * @description
 */
public interface FeatureVariableService {
    Map<String,String> getScripts(FeatureModuleEntity featureModuleEntity);

    Object entryPoint(Map<String,Object> params, String name);
}
