package com.value.decision.model.rdenew.vo;

import lombok.Data;

@Data
public class BankClassificationVO {

    /**
     * 规则
     */
    private String ruleStr;

    /**
     * 流水分类
     */
    private String purposeCategory;

    /**
     * 经营性类别 1-经营性收入 2-非经营性收入 3-经营性支出 4-非经营性支出
     */
    private String categoryType;

    /**
     * 规则描述
     */
    private String content;
}
