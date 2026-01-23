package com.value.data.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 生成DRL脚本数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
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
