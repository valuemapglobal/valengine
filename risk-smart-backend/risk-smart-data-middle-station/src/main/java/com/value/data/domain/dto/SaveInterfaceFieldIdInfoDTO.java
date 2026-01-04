package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 新增接口参数
 */
@Data
public class SaveInterfaceFieldIdInfoDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 接口编号
     */
    @NotBlank(message = "interfaceNo 参数错误")
    @Size(min = 1, max = 200, message = "interfaceNo应该在1-200字符之间")
    private String interfaceNo;

    /**
     * 接口管理唯一标识
     */
    @NotBlank(message = "manageNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "ManageNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String manageNo;

    /**
     * 参数名称
     */
    @NotBlank(message = "interfaceFieldIdName 参数错误")
    @Size(min = 1, max = 200, message = "interfaceFieldIdName应该在1-200字符之间")
    private String interfaceFieldIdName;

    /**
     * 参数别名
     */
    @NotBlank(message = "interfaceFieldIdAlias 参数错误")
    @Size(min = 1, max = 200, message = "interfaceFieldIdAlias应该在1-200字符之间")
    private String interfaceFieldIdAlias;

    /**
     * 参数说明
     */
    @Size(max = 300, message = "interfaceFieldIdDescription应该在1-300字符之间")
    private String interfaceFieldIdDescription;

    /**
     * 参数类型(0-入参，1-出参)
     */
    @NotBlank(message = "interfaceFieldIdType 参数错误")
    @Pattern(regexp = "^[01]$", message = "interfaceFieldIdType 只允许 0-入参，1-出参")
    private String interfaceFieldIdType;

    /**
     * 数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组)
     */
    @NotBlank(message = "interfaceFieldIdDataType 参数错误")
    @Pattern(regexp = "^[01234567]$", message = "interfaceFieldIdDataType 只允许 0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件，6-布尔，7-小数")
    private String interfaceFieldIdDataType;

    /**
     * 入参必填项(0-是，1-否)
     */
    @NotBlank(message = "interfaceFieldIdRequired 参数错误")
    @Pattern(regexp = "^[01]$", message = "interfaceFieldIdRequired 只允许 0-必填，1-非必填")
    private String interfaceFieldIdRequired;

    /**
     * 接口序号
     */
    @Pattern(regexp = "\\d{0,11}", message = "interfaceFieldIdIndex 只允许数值格式，长度最大为11位")
    private String interfaceFieldIdIndex;

    /**
     * 参数备注
     */
    @Size(min = 1, max = 300, message = "interfaceFieldIdRemark应该在1-300字符之间")
    private String interfaceFieldIdRemark;

    /**
     * 默认值
     */
    @Size(max = 200, message = "interfaceFieldIdDefultValue应该在0-200字符之间")
    private String interfaceFieldIdDefultValue;

    /**
     * 父级属性
     */
    @Size(max = 200, message = "interfaceFieldIdFather应该在0-200字符之间")
    private String interfaceFieldIdFather;

}
