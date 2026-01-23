package com.value.decision.process.dto;

import lombok.Data;

@Data
public class SubmitDTO {
    //身份证
    private String number;

    //姓名
    private String name;

    //手机号
    private String mobile;

    //流程id
    private Integer processStrategyId;


}
