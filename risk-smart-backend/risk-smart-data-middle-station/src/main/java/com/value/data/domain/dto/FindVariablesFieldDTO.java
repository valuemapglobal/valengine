package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 获取特征变量分组
 */
@Data
public class FindVariablesFieldDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 特征变量标识
     */
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String groupNo;

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
