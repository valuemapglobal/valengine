package com.value.data.domain.dto;

import lombok.Data;

/**
 * @author Vida
 * @date 2023年08月22日 13:54
 * @description 并且条件
 */
@Data
public class ConditionDTO {
    /**
     * 选择的元数据或特征变量对象
     */
    private SelectObjDTO selectObj;
    /**
     * 运算符
     */
    private String operator;
    /**
     * 条件判断结果
     */
    private ResultDTO result;
}
