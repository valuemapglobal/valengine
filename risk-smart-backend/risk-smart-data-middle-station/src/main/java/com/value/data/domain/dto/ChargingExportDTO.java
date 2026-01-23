package com.value.data.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargingExportDTO {

    //客户经理名称
    @ApiModelProperty(name = "用户名称")
    private String createBy;
    //调用时间
    @ApiModelProperty(name = "调用时间")
    private String createTime;
    //接口名称
    @ApiModelProperty(name = "接口名称")
    private String interfaceName;
    //接口客户类型
    @ApiModelProperty(name = "接口客户类型")
    private String interfaceType;
    //价格
    @ApiModelProperty(name = "价格")
    private BigDecimal price;
    //进件编号
    @ApiModelProperty(name = "进件编号")
    private String orderNo;
    @ApiModelProperty(name = "是否计费")
    private String chargingFlag;
}
