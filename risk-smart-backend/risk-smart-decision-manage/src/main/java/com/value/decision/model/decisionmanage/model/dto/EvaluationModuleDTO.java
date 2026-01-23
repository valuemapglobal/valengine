package com.value.decision.model.decisionmanage.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 评估报告中的模型模块
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationModuleDTO {

    private String moduleTitle;
    private String status;
    private String keyResult;
    private String modelType;
    private String modelId;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private BigDecimal finalScore;
    private List<EvaluationRuleDetailDTO> details;

    // 评级额度相关字段(moduleId=2/3使用)
    private String rate;                    // 评级结果(AAA/AA/A/BBB/BB/B/C/D/E)
    private Object quota;                   // 授信额度(数字或"未计算")
    private String quotaCalculationDesc;    // 额度计算说明
}