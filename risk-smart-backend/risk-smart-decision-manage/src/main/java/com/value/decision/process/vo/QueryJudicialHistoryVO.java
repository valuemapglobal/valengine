package com.value.decision.process.vo;

import lombok.Data;

@Data
public class QueryJudicialHistoryVO {
    //企业或个人标识, 1-企业，2-个人
    private Integer flag;
    private String judicialNo;
    private Integer userId;
    private Integer deptId;
}
