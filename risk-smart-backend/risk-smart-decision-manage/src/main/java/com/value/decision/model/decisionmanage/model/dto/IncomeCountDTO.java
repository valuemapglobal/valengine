package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;

@Data
public class IncomeCountDTO {
    private Integer pageNum;
    private Integer pageSize;
    //客户类型
    private Integer customerType;
    //时间维度
    private String timeDimension;

    private Long deptId;

}
