package com.value.data.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Vida
 * @date 2023年08月22日 13:52
 * @description 条件
 */
@Data
public class ConditionArrayDTO {
    /**
     * 并且条件
     */
    private List<ConditionDTO> condition;
}
