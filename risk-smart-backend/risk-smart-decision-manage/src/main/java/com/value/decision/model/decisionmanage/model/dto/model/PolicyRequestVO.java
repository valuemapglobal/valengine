package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

@Data
public class PolicyRequestVO {

    /**
     * 流程ID
     */
    private Integer processStrategyId;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 策略场景标识
     */
    private Integer ruleCode;
}
