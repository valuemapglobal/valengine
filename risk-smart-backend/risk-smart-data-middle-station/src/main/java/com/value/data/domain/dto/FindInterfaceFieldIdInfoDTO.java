package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 获取接口参数信息
 */
@Data
public class FindInterfaceFieldIdInfoDTO implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 接口管理唯一标识
     */
    @NotBlank(message = "manageNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "ManageNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String manageNo;

    /**
     * 参数名称
     */
    @Size(max = 30, message = "interfaceFieldIdName应该在0-30字符之间")
    private String interfaceFieldIdName;

    /**
     * 页码
     */
    @Min(value = 1, message = "pageNum 低于最小值 1页")
    private Integer pageNum;

    /**
     * 数量
     */
    @Min(value = 1, message = "pageSize 低于最小值 1条")
    private Integer pageSize;


    /**
     * 参数类型(0-入参，1-出参)
     */
    private Integer interfaceFieldIdType;
}
