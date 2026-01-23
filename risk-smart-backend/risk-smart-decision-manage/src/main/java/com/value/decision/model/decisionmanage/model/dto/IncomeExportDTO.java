package com.value.decision.model.decisionmanage.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IncomeExportDTO {


    /**
     * 进件编号
     */
    @ApiModelProperty(value = "进件编号")
    private String incomeNumber;


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
     * 电话电话
     */
    @ApiModelProperty(value = "电话电话")
    private String mobile;

    /**
     * 身份证号/社会信用代码
     */
    @ApiModelProperty(value = "身份证号/社会信用代码")
    private String idNumber;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
