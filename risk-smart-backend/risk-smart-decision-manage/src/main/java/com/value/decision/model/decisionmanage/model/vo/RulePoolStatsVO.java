package com.value.decision.model.decisionmanage.model.vo;

import lombok.Data;

/**
 * 规则池统计VO
 * @author Claude Code
 * @date 2025-12-01
 */
@Data
public class RulePoolStatsVO {

    // 总规则数
    private Long totalRules;

    // 总规则数较上月环比（百分比，如：12.5表示增长12.5%）
    private Double totalRulesRate;

    // 启用中规则数
    private Long activeRules;

    // 启用中规则数较上月环比（百分比）
    private Double activeRulesRate;

    // 规则覆盖产品数
    private Long productCount;

    // 规则覆盖产品数较上月环比（百分比）
    private Double productCountRate;

    // 规则覆盖场景数
    private Long sceneCount;

    // 规则覆盖场景数较上月环比（百分比）
    private Double sceneCountRate;
}
