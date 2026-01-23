package com.value.decision.model.decisionmanage.model;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class FindExceptionLogDTO {
    @NotNull(message = "pageNum 参数错误")
    private Integer pageNum;
    @NotNull(message = "pageSize 参数错误")
    private Integer pageSize;

    private Integer operationType;
    private String startTime;
    private String endTime;
}
