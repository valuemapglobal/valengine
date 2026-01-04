package com.value.decision.model.decisionmanage.model.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class JudicialRuleVO {

    //企业名称
    private String enterpriseName;

    //企业名称
    private String companyName;

    //企业名称
    private String ckey;

    //身份证
    private String number;

    //姓名
    private String name;

    //手机号
    private String mobile;

    //模型唯一标识
    private String moduleId;

    //业务标识
    @NotBlank(message = "交易号不可为空")
    private String orderNo;

    //模型标识（1.借款人，2.实控人，3.担保人 4.共债人（配偶,5.企业）
    private String modelNo;

    //统一社会信用代码
    private String creditCode;

    //部门编号
    private Integer deptId;

    //用户编号
    private Integer userId;

    //用户名称
    private String nickName;
}
