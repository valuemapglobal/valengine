package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 移除接口参数
 */
@Data
public class RemoveInterfaceFieldIdInfoDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 接口参数唯一标识
     */
    @NotBlank(message = "fieldIdNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "fieldIdNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String fieldIdNo;
}
