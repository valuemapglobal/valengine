package com.value.decision.process.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @description：TODO
 * @author： andera
 * @create： 2023/10/19 10:50
 */
@Data
public class RuleResultDTO {

    //企业名称
    private String enterpriseName;

    //企业名称
    private String companyName;

    //企业名称
    private String ckey;

    //模型唯一标识
    private String moduleId;

    //模型标识（1.借款人，2.实控人，3.担保人 4.共债人（配偶,5.企业）
    private String modelNo;

    //统一社会信用代码
    private String creditCode;

    //业务标识
    @NotBlank(message = "交易号不可为空")
    private String orderNo;

    //流程id
    private Integer processStrategyId;

    /**
     * 部门ID
     */
    private Integer deptId;
    /**
     * 用户ID
     */
    private Integer userId;

    //申请用户
    private String applyUserName;

    //授信状态 1 授信中  2 通过 3 拒绝
    private Integer creditStatus;

    //身份证
    private String number;

    //姓名
    private String name;

    //手机号
    private String mobile;

    //txd用户授权流水号
    private String userOrderNo;

}
