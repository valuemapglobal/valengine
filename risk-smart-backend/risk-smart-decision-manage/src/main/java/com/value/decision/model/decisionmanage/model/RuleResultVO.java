package com.value.decision.model.decisionmanage.model;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @description：TODO
 * @author： andera
 * @create： 2023/10/19 10:50
 */
@Data
public class RuleResultVO {

    //企业名称
    @NotBlank(message = "企业名称不可为空")
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
    @NotBlank(message = "模型标识不可为空")
    private String modelNo;

    //统一社会信用代码
    private String creditCode;

}
