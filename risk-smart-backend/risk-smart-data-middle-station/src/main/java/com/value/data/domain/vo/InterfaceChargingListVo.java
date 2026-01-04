package com.value.data.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InterfaceChargingListVo {
    //订单编号
    private String orderId;
    //客户经理名称
    private String createBy;
    //调用时间
    private String createTime;
    //接口名称
    private String interfaceName;
    //接口客户类型
    private Integer interfaceType;
    //价格
    private BigDecimal price;
    //传递参数
    private String param;
    //进件编号
    private String orderNo;
    //计费标识
    private Integer chargingFlag;
}
