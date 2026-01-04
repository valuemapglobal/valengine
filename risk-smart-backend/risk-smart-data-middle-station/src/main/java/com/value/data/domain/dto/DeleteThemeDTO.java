package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Vida
 * @date 2023年08月16日 14:56
 * @description
 */
@Data
public class DeleteThemeDTO implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "themeNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String themeNo;
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceSourceNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceSourceNo;
}
