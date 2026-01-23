package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 删除主题数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class DeleteThemeDTO implements Serializable {
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "themeNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String themeNo;
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceSourceNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceSourceNo;
}
