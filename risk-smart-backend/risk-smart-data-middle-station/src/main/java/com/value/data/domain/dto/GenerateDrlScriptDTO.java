package com.value.data.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Vida
 * @date 2023年08月22日 13:47
 * @description
 */
@Data
public class GenerateDrlScriptDTO {
    /**
     * 指标对象code
     */
    private String code;
    /**
     * 默认3
     */
    private String dataType = "3";
    /**
     * 指标对象code
     */
    private String objStats;
    /**
     * 指标结果
     */
    private String objResult;
    /**
     * 默认1
     */
    private String objResultType = "1";
    /**
     * 条件集合
     */
    private List<ConditionArrayDTO> conditionArray;
}
