package com.value.decision.model.decisionmanage.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RulePoolVO {
    private Integer id;
    //策略
    private String strategy;

    //规则组
    private String ruleGroup;

    //规则CODE
    private String ruleCode;

    //规则描述
    private String ruleDesc;

    //风险等级（高风险、中风险、低风险）
    private String riskLevel;

    //命中动作（拒绝、转人工、通过）
    private String hitAction;

    //命中动作展示（格式：强拒绝、转人工、强拒绝-转人工、-）
    private String hitActionDisplay;

    //状态（启用中、已禁用）
    private String status;

    //参考产品
    private String referenceProduct;

    //业务场景（反欺诈、准入）
    private String businessScene;

    //模型类型（规则、分类、评分）
    private String modelType;

    //创建时间
    private LocalDateTime createTime;
}
