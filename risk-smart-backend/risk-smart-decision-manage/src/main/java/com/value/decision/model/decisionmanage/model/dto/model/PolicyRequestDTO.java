package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

@Data
public class PolicyRequestDTO {

    /**
     * 参数名称
     */
    private String nameZh;

    /**
     * 参数名
     */
    private String name;

    /**
     * 参数类型
     */
    private Integer type;

    /**
     * 参数类型名称
     */
    private String typeName;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * 参数值
     */
    private String value;
}
