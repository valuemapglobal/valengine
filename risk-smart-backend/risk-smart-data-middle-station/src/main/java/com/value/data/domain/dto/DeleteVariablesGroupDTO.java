package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;

/**
 * @author Vida
 * @date 2023年08月10日 14:50
 * @description
 */
@Data
public class DeleteVariablesGroupDTO {
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String groupNo;


    /**
     * 关联的接口
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceManageNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceManageNo;
}
