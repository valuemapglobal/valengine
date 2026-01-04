package com.value.decision.model.decisionmanage.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 评估报告中具体的规则命中详情
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationRuleDetailDTO {

    private Long id;
    private String riskLevel;
    private String ruleCode;
    private String dimension;
    private String hitAction;

    // 用于分类模型，存放动态表头和数据
    private List<Map<String, String>> dynamicHeaders;
    private List<Map<String, Object>> dynamicData;
}