package com.value.data.service;

import com.value.data.domain.dto.InterfaceQueryDTO;

import java.util.Map;

/**
 * @author Vida
 * @date 2025年03月31日 17:45
 * @description
 */
public interface DecisionService {
    Map<String,Map<String,Object>> decisionQI(InterfaceQueryDTO dto);
}
