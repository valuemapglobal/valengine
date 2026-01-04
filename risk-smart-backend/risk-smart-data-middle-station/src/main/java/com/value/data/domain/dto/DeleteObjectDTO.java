package com.value.data.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class DeleteObjectDTO implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 分析指标对象标识
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsObjectNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsObjectNo;



}
