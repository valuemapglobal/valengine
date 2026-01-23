package com.value.data.domain.dto;


import lombok.Data;

import jakarta.validation.constraints.Pattern;

/**
 * 元数据对象分组数据传输对象
 */
@Data
public class DeleteGroupDTO {

    /**
     * 对象分组序号
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$",message = "groupNo格式不合法")
    private String groupNo;

    /**
     * 关联的接口
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$",message = "interfaceManageNo 格式不合法")
    private String interfaceManageNo;
}