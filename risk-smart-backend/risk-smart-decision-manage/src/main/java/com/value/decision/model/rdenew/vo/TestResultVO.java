package com.value.decision.model.rdenew.vo;

import lombok.Data;

@Data
public class TestResultVO {

    /**
     * 序号
     */
    private Integer id;

    /**
     * 规则名称
     */
    private String ruleCode;

    /**
     * 规则描述
     */
    private String ruleContent;

    /**
     * 命中数量
     */
    private String ruleNum;
}
