package com.value.decision.model.rdenew.vo;

import lombok.Data;

import java.util.List;

/**
 * and条件
 */
@Data
public class RdeRiskVariableArrayVO {

    private Integer id;

    // 或条件
    private List<RdeRiskVariableConditionNewVO> condition;
}
