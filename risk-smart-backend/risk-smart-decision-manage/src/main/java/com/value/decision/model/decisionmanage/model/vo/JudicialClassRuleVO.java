package com.value.decision.model.decisionmanage.model.vo;

import lombok.Data;

@Data
public class JudicialClassRuleVO {

    /**
     * 规则编号
     */
    private String code;

    /**
     * 风险等级
     */
    private String level;

    /**
     * 说明
     */
    private String content;

    /**
     * 是否强拒绝1是0否
     */
    private String stronglyReject;

    /**
     * 规则组
     */
    private String groupName;
}
