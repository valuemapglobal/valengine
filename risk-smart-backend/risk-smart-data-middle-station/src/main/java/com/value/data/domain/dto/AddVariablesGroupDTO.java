package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 增添分组
 */
@Data
public class AddVariablesGroupDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 特征变量标识
     */
    @NotBlank(message = "no 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "no只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String no;

    /**
     *  分组名称
     */
    @NotBlank(message = "groupName 参数错误")
    @Size(min = 1, max = 30, message = "groupName应该在1-30字符之间")
    private String groupName;

    /**
     *  分组code
     */
    @NotBlank(message = "groupCode 参数错误")
    @Size(min = 1, max = 30, message = "groupCode应该在1-30字符之间")
    private String groupCode;

    /**
     * 关联的接口
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$",message = "interfaceManageNo 格式不合法")
    private String interfaceManageNo;


    /**
     * 关联数据场景
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$",message = "interfaceSourceNo 格式不合法")
    private String interfaceSourceNo;

    /**
     * 版本号
     */
    private String interfaceVersion;

}
