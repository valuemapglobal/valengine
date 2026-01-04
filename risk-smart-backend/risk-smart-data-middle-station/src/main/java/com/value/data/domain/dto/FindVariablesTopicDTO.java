package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 获取主题
 */
@Data
public class FindVariablesTopicDTO implements Serializable {
    private static final long serialVersionUID=1L;
    /**
     * 页码
     */
    @NotNull(message = "pageNum 参数错误")
    @Min(value = 1, message = "pageNum 低于最小值 1页")
    private Integer pageNum;

    /**
     * 数量
     */
    @NotNull(message = "pageSize 参数错误")
    @Max(value = 30, message = "pageSize 高于最大值 30条")
    @Min(value = 1, message = "pageSize 低于最小值 1条")
    private Integer pageSize;
}
