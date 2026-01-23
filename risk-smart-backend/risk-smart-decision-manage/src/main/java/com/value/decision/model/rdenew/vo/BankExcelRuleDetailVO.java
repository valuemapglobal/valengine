package com.value.decision.model.rdenew.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 银行流水规则引擎属性  -- 规则
 * @create 2023/4/13 10:25
 */

@Data
public class BankExcelRuleDetailVO {

    //对手方名称
    private String dealAccountName;

    //摘要
    private String remark;

    //收入
    private BigDecimal borrow;

    //支出
    private BigDecimal lend;

    //交易时间
    private String tradeDate;

    //用途
    private String purpose;
}
