package com.value.data.domain.vo;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 规则视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class RuleVO implements Serializable {
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

    /**
     * 判断条件
     */
    @NotEmpty
    private List<RuleDetailsVO> condition;

}
