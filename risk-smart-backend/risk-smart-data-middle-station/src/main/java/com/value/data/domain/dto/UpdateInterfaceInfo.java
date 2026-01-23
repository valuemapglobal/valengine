package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新接口信息
 */
@Data
public class UpdateInterfaceInfo implements Serializable {

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
     * 接口编号
     */
    @NotBlank(message = "interfaceNo 参数错误")
    @Size(min = 1, max = 50, message = "interfaceNo应该在1-50字符之间")
    private String interfaceNo;

    /**
     * 接口名称
     */
    @NotBlank(message = "interfaceName 参数错误")
    @Size(min = 1, max = 30, message = "interfaceName应该在1-30字符之间")
    private String interfaceName;

    /**
     * 接口类型 0个人接口，1 企业接口，2 司法接口
     */
    @Pattern(regexp = "^[012]$", message = "interfaceType 只允许 0-个人接口，1-企业接口，2-司法接口")
    private String interfaceType;

    /**
     * 接口描述
     */
    @Size(max = 30, message = "interfaceDescription应该在0-30字符之间")
    private String interfaceDescription;

    /**
     * 接口特点
     */
    @Size(max = 200, message = "interfaceFeatures应该在0-200字符之间")
    private String interfaceFeatures;

    /**
     * 接口场景
     */
    @Size(max = 200, message = "interfaceScenes应该在0-200字符之间")
    private String interfaceScenes;

    /**
     * 接口覆盖体量
     */
    @Size(max = 30, message = "interfaceCoveringVolume应该在0-30字符之间")
    private String interfaceCoveringVolume;

    /**
     * 接口标签
     */
    @Size(max = 30, message = "interfaceTag应该在0-30字符之间")
    private String interfaceTag;

    /**
     * 接口网址
     */
    @NotBlank(message = "interfaceLink 参数错误")
    private String interfaceLink;

    /**
     * 接口配额
     */
    @Pattern(regexp = "^[0-9]{0,30}$", message = "interfaceQuota只允许数字，且长度不超过30位")
    private String interfaceQuota;

    /**
     * 接口序号
     */
    @Pattern(regexp = "^[0-9]{0,30}$", message = "interfaceQuota只允许数字，且长度不超过30位")
    private String interfaceIndex;

//    /**
//     * 0 元数据，1 特征变量
//     */
//    @NotNull(message = "interfaceDataType")
//    @Min(value = 0, message = "interfaceDataType 低于界限")
//    @Max(value = 1, message = "interfaceDataType 超出界限")
//    private Integer interfaceDataType;

    /**
     * 0-Json，1-from-data
     */
    @NotBlank(message = "paramType 参数错误")
    @Pattern(regexp = "^[01]$", message = "paramType 只允许 0-Json，1-from-data")
    private String paramType;

    /**
     * 0-POST, 1-GET
     */
    @NotBlank(message = "requestType 参数错误")
    @Pattern(regexp = "^[01]$", message = "requestType 只允许 0-POST，1-GET")
    private String requestType;


    /**
     * 版本号
     */
    private String interfaceVersion;

    /**
     * 返回值类型（0-对象，5-数组）
     */
    private Integer returnType;

    /**
     * 单价
     */
    private Float price;

    /**
     * 超时时间(毫秒)
     */
    @Min(value = 1, message = "timeout必须大于0")
    private Integer timeout;
}
