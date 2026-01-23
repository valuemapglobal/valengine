package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 规则分页视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
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
