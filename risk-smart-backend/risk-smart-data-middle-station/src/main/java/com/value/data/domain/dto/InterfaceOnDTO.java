package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 接口应用开关
 */
@Data
public class InterfaceOnDTO implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 接口信息唯一标识
     */
    @NotBlank(message = "manageNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "ManageNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String manageNo;

    /**
     * 接口供应商唯一标识
     */
    @NotBlank(message = "sourceNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "sourceNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String sourceNo;

    /**
     * 0 开启，1 关闭
     */
    @NotBlank(message = "interfaceOn 参数错误")
    @Pattern(regexp = "^[01]$", message = "interfaceOn 只允许 0-开启，1-关闭")
    private String interfaceOn;
}
