package com.value.data.domain.dto;

import com.value.data.constant.PageConstant;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 元数据主题数据传输对象
 */
@Data
public class RdeRiskVariableThemeDTO implements Serializable {

    private Integer id;

    /**
     * 主题名称
     */
    @NotBlank(message = "主题名称不能为空")
    @Size(min = 1, max = 30, message = "主题名称不能超过30个字符")
    private String name;

    /**
     * 包名称
     */
    @NotBlank(message = "包名称不能为空")
    private String keycode;

    /**
     * 主题类型
     */
    private String packageType;

    /**
     * 雪花标识
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "themeNo不合法")
    private String themeNo;

    @Min(value = 1, message = "pageNum不能小于1")
    private Long pageNum = PageConstant.pageNum;

    @Max(value = 30, message = "pageSize不能大于30")
    @Min(value = 1, message = "pageSize不能小于1")
    private Long pageSize = PageConstant.pageSize;

    /**
     * 关联数据场景
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "themeNo不合法")
    private String interfaceSourceNo;
}
