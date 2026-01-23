package com.value.data.domain.dto;


import lombok.Data;

import jakarta.validation.constraints.Pattern;

/**
 * 元数据属性数据传输对象
 */
@Data
public class DeleteRecordDTO {

    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$",message = "recordNo不合法")
    private String recordNo;

    /**
     * 关联的接口参数
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceFieldIdManage 不合法")
    private String interfaceFieldIdManage;


}