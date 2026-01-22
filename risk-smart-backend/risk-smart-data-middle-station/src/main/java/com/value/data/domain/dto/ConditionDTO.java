package com.value.data.domain.dto;

import lombok.Data;

/**
 * 并且条件数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
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
