package com.value.data.domain.dto;

import lombok.Data;

/**
 * @author Vida
 * @date 2023年08月22日 14:04
 * @description 条件判断结果
 */
@Data
public class ResultDTO {
    /**
     * 条件结果值
     */
    private String result;
    /**
     * 值类型（与属性类型一致）
     */
    private String resultType;
}
