package com.value.decision.process.vo;

import lombok.Data;

@Data
public class InitJudicialReportVO {
    //企业或个人标识, 1-企业，2-个人
    private Integer flag ;
    private String name;
    private String idNumber;
    private String mobile;
    //直接传字符串，例子："全部"、"原告"、"被告"、"第三人"
    private String caseRole;
    //直接传字符串例子："全部"、"未结案"、"已结案"
    private String caseProgress;
    //数据维度 0-债务债权、1-案件类型
    private Integer dataDimension;
    private Integer userId;
    private Integer deptId;
}
