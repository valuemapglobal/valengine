package com.value.data.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 条件
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class ConditionArrayDTO {
    /**
     * 并且条件
     */
    private List<ConditionDTO> condition;
}
