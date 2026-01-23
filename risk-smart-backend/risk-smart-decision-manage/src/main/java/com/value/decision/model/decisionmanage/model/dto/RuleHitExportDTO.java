package com.value.decision.model.decisionmanage.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RuleHitExportDTO {

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;

    /**
     * 身份证号/社会信用代码
     */
    @ApiModelProperty(value = "身份证号/社会信用代码")
    private String idNumber;
    /**
     * 接口类型(0-营销初筛,1-初筛,2-准入,3个体评分,4-财务评分)
     */
    @ApiModelProperty(value = "业务场景")
    private String interfaceType;

    /**
     * 风险等级
     */
    @ApiModelProperty(value = "风险等级")
    private Integer riskLevel;

    /**
     * 评分结果
     */
    @ApiModelProperty(value = "评分结果")
    private Double scoreResult;

    /**
     * 决策说明
     */
    @ApiModelProperty(value = "决策说明")
    private String decisionDesc;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;


}
