package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Pattern;

/**
 * 删除变量字段数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class DeleteVariablesFieldDTO {
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String fieldNo;
    /**
     * 关联的接口参数
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceFieldIdManage 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String interfaceFieldIdManage;
}
