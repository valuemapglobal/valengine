package com.value.data.domain.dto;

import com.value.data.constant.PageConstant;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 元数据对象分组数据传输对象
 */
@Data
public class RdeRiskVariableGroupDTO {
    private Integer id;

    /**
     * 对象名称
     */
    @NotBlank(message = "对象名称不能为空")
    @Size(min = 1, max = 30, message = "对象名称不能超过30个字符")
    private String name;

    /**
     * code
     */
    @Size(min = 1, max = 30, message = "对象code不能超过30个字符")
    private String keycode;

    /**
     * 父级名称
     */
    private String parentName;

    /**
     * 集合名称:分析对象
     */
    private String listName;

    /**
     * 类型对象 int 字符串
     */
    @NotBlank(message = "对象类型不能为空")
    private String type;

    /**
     * 备注-别名
     */
    @Size(min = 1, max = 30, message = "对象别名不能超过30个字符")
    private String remark;

    /**
     * 主题编号
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "themeNo格式不合法")
    private String themeNo;

    /**
     * 对象分组序号
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "groupNo格式不合法")
    private String groupNo;

    @Min(value = 1, message = "pageNum不能小于1")
    private Long pageNum = PageConstant.pageNum;

    @Max(value = 30, message = "pageSize不能大于30")
    @Min(value = 1, message = "pageSize不能小于1")
    private Long pageSize = PageConstant.pageSize;

    /**
     * 关联的接口
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceManageNo 格式不合法")
    private String interfaceManageNo;

    /**
     * 关联的接口场景
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceSourceNo 格式不合法")
    private String interfaceSourceNo;

    /**
     * 版本号
     */
    private String interfaceVersion;
}
