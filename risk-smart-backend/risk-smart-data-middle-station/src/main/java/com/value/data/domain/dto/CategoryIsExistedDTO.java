package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author Vida
 * @date 2023年08月14日 13:36
 * @description
 */
@Data
public class CategoryIsExistedDTO {

    /**
     * 分析指标标识
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsNo;

    /**
     * 分类名称
     */
    @Size(min = 1, max = 30, message = "categoryName应该在1-30字符之间")
    private String categoryName;

    /**
     * 分类code
     */
    @Size(min = 1, max = 30, message = "categoryCode应该在1-30字符之间")
    private String categoryCode;
}
