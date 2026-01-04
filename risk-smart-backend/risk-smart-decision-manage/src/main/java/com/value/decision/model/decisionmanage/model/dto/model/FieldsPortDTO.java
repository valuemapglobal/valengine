package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 接口出入参类
 */
@Data
public class FieldsPortDTO {

    /**
     * 接口编号
     */
    private String interfaceName;

    /**
     * 接口名称
     */
    private String interfaceNameZh;

    /**
     * 接口类型(对象/数组)  0对象 5集合
     */
    private Integer responseType;

    /**
     * 字段属性以及类型
     */
    private List<FieldsDTO> attributeList;
}
