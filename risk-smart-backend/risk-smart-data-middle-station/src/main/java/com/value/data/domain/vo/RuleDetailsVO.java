package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分析指标规则详情表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class RuleDetailsVO implements Serializable {

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
     * 分析指标规则详情标识
     */
    private String ruleDetailsNo;

    /**
     * 判断条件一级标题
     */
    private String packageNo;

    /**
     * 判断条件二级标题
     */
    private String objectNo;

    /**
     * 判断条件三级标题
     */
    private String attributeNo;

    /**
     * 运算符
     */
    private String operator;

    /**
     * 值
     */
    private String value;

    /**
     * 父标识
     */
    private List<RuleDetailsVO> condition;

}
