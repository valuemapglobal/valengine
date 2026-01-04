package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 获取供应商信息
 */
@Data
public class FindSourceInfoDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 页码
     */
    @Min(value = 1, message = "pageNum 低于最小值 1页")
    private Integer pageNum;

    /**
     * 数量
     */
    @Min(value = 1, message = "pageSize 低于最小值 1条")
    private Integer pageSize;

    /**
     * 供应商名称
     */
    @Size(min = 1, max = 30, message = "sourceName 应该在1-30字符之间")
    private String sourceName;

    /**
     * 0 元数据，1 特征变量，2 分析指标
     */
    private List<Integer> interfaceDataType;
}
