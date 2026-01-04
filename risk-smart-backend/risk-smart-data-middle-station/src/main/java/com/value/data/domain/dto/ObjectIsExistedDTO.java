package com.value.data.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * <p>
 * 分析指标对象表
 * </p>
 *
 * @author Vida
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ObjectIsExistedDTO implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;


    /**
     * 分析指标标识
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsNo;


    /**
     * 分析指标对象标识
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsObjectNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsObjectNo;

    /**
     * 分类名称
     */
    @Size(min = 1, max = 30, message = "categoryName应该在1-30字符之间")
    private String objectName;

    /**
     * 分类code
     */
    @Size(min = 1, max = 30, message = "categoryCode应该在1-30字符之间")
    private String objectCode;


}
