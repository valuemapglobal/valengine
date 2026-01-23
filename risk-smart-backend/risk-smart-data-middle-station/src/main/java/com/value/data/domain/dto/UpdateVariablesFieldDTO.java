package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 更新属性
 */
@Data
public class UpdateVariablesFieldDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 特征变量标识
     */
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String no;

    /**
     * 分组标识
     */
    @NotBlank(message = "groupNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "groupNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String groupNo;

    /**
     * 属性标识
     */
    @NotBlank(message = "groupNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "groupNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String fieldNo;

    /**
     *  属性名称
     */
    @NotBlank(message = "fieldName 参数错误")
    @Size(min = 1, max = 30, message = "fieldName应该在1-30字符之间")
    private String fieldName;

    /**
     *  分组code
     */
    @NotBlank(message = "fieldCode 参数错误")
    @Size(min = 1, max = 30, message = "fieldCode应该在1-30字符之间")
    private String fieldCode;

    /**
     * 属性类型 0 对象、1 集合、2 字符、3 布尔、4 整数、5 浮点数
     */
    @NotNull(message = "fieldType 参数错误")
    @Min(value = 2, message = "fieldType 数值低于2")
    @Max(value = 5, message = "fieldType 数值高于5")
    private Integer fieldType;

    /**
     * 属性备注
     */
    @Size(max = 30, message = "fieldCode应该在0-200字符之间")
    private String fieldRemark;


    /**
     * 关联的接口参数
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceFieldIdManage 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceFieldIdManage;

    private String parentNo;
}
