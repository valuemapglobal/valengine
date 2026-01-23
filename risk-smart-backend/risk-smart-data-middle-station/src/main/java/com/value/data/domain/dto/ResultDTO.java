package com.value.data.domain.dto;

import lombok.Data;

/**
 * 条件判断结果数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
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
