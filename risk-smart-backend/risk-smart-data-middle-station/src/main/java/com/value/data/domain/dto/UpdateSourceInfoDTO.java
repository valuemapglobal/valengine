package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新供应商信息
 */
@Data
public class UpdateSourceInfoDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 来源唯一标识
     */
    @NotBlank(message = "sourceNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "sourceNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String sourceNo;

    /**
     * 数据名称
     */
    @NotBlank(message = "dataName 参数错误")
    @Size(min = 1, max = 30, message = "dataName应该在1-30字符之间")
    private String dataName;

    /**
     * 来源
     */
    @NotBlank(message = "source 参数错误")
    @Size(min = 1, max = 30, message = "source应该在1-30字符之间")
    private String source;

    /**
     * 网址
     */
    @NotBlank(message = "webLink 参数错误")
    private String webLink;

    /**
     * 负责人
     */
    @NotBlank(message = "webLink 参数错误")
    @Size(min = 1, max = 30, message = "adminName应该在1-30字符之间")
    private String adminName;

    /**
     * 联系方式
     */
    @NotBlank(message = "contactDetails 参数错误")
    private String contactDetails;

    /**
     * 0 元数据，1 特征变量
     */
    @NotBlank(message = "interfaceDataType 参数错误")
    @Pattern(regexp = "^[012]$", message = "interfaceDataType 只允许 0-元数据，1-特征变量，2-分析指标")
    private String interfaceDataType;
}
