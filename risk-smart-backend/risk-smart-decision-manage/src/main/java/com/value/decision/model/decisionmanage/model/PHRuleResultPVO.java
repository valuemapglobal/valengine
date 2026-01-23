package com.value.decision.model.decisionmanage.model;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * PH规则结果PVO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class PHRuleResultPVO {

    //企业名称
    private String enterpriseName;

    //企业名称
    private String companyName;

    //企业名称
    private String ckey;

    //身份证
    @NotBlank(message = "身份证不可为空")
    private String idNumber;

    //姓名
    @NotBlank(message = "姓名不可为空")
    private String userName;

    //手机号
    @NotBlank(message = "手机号不可为空")
    private String phone;

    //模型唯一标识
    private String moduleId;

    //业务标识
    @NotBlank(message = "交易号不可为空")
    private String orderNo;

    private String modelNo;
    
    //行驶证
    @NotBlank(message = "行驶证不可为空")
    private String credentialNo;


}
