package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;

@Data
public class IncomeListDTO {
    private Integer pageNum;
    private Integer pageSize;
    private String startTime;
    private String endTime;
    private Long deptId;
}
