package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 额度区间DTO
 */
@Data
public class QuotaRangeDTO {

    /**
     * 区间ID
     */
    private Integer id;

    /**
     * 区间标签
     */
    private String label;

    /**
     * 额度下限（万元）
     */
    private BigDecimal quotaMin;

    /**
     * 额度上限（万元）
     */
    private BigDecimal quotaMax;

    /**
     * 是否包含下限：1包含 0不包含
     */
    private Integer quotaIncludeMin;

    /**
     * 是否包含上限：1包含 0不包含
     */
    private Integer quotaIncludeMax;
}
