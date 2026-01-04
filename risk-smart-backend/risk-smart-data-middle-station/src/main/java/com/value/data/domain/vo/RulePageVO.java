package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Vida
 * @date 2023年08月14日 19:28
 * @description
 */
@Data
public class RulePageVO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 分析指标标识
     */
    private String analysisIndicatorsNo;

    /**
     * 分析指标对象标识
     */
    private String analysisIndicatorsObjectNo;

    /**
     * 分析指标规则标识
     */
    private String analysisIndicatorsRuleNo;

    /**
     * 规则结果
     */
    private String ruleResult;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 描述
     */
    private String remark;

}
