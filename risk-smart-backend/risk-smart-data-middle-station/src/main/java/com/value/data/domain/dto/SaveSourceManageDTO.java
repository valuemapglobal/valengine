package com.value.data.domain.dto;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 供应商增添
 */
@Data
public class SaveSourceManageDTO implements Serializable {

    private static final long serialVersionUID=1L;

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
