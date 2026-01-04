package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Vida
 * @date 2023年08月14日 11:44
 * @description
 */
@Data
public class GetCategoryDTO implements Serializable {

    private static final long serialVersionUID=1L;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsNo;
}
