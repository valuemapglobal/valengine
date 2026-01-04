package com.value.decision.model.rdenew.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * or条件
 */
@Data
public class RdeRiskVariableConditionNewVO {

    private Integer id;
    // 条件
    private String operator;
    // 值结果 加 类型
    // result      结果值
    // resultType  结果值类型
    // 1 字符型
    // 2 布尔型
    // 3 整数型
    // 5 浮点型
    private Map<String, Object> result;
    // 选择对象
    private IdentificationVO selectObj;
    /**
     * 对象层级标识
     */
    private List<String> objectLevel;

    // 是否属于 集合size判断  0为否 1为是
    private Integer arraySize;
}
