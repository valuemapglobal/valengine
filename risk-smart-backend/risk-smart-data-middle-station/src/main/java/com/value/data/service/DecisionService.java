package com.value.data.service;

import com.value.data.domain.dto.InterfaceQueryDTO;

import java.util.Map;

/**
 * 决策服务接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface DecisionService {
    Map<String,Map<String,Object>> decisionQI(InterfaceQueryDTO dto);
}
