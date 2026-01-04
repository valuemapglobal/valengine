package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author Vida
 * @date 2023年08月10日 15:52
 * @description
 */
@Data
public class FieldIsExistedDTO {
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String groupNo;


    /**
     *  属性名称
     */
    @Size(min = 1, max = 30, message = "fieldName应该在1-30字符之间")
    private String fieldName;

    /**
     *  分组code
     */
    @Size(min = 1, max = 30, message = "fieldCode应该在1-30字符之间")
    private String fieldCode;
}
