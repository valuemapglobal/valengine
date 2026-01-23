package com.value.data.domain.dto;

import lombok.Data;

@Data
public class InterfaceChargingDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String startTime;
    private String endTime;
    //用户名称
    private String userName;
    //供应商编号,用于区分
    private String interfaceSourceNo;
    private Long deptId;
}
