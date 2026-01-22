package com.value.data.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 更新分析指标分类数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateCategoryDTO implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 关联元数据标识
     */
    private List<@Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "metaNo只允许英文、数字、下划线、冒号，且长度不超过64位") String> metaNo;

    /**
     * 关联特征变量标识
     */
    private List<@Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "VariableNo只允许英文、数字、下划线、冒号，且长度不超过64位") String> VariableNo;

    /**
     * 分析指标标识
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsNo;

    /**
     * 分类名称
     */
    @NotBlank(message = "categoryName不能为空")
    @Size(min = 1, max = 30, message = "categoryName应该在1-30字符之间")
    private String categoryName;

    /**
     * 分类code
     */
    @NotBlank(message = "categoryCode不能为空")
    @Size(min = 1, max = 30, message = "categoryCode应该在1-30字符之间")
    private String categoryCode;

    /**
     * 版本号
     */
    private String interfaceVersion;
}
