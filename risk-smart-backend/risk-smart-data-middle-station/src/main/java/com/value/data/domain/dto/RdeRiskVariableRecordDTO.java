package com.value.data.domain.dto;

import com.value.data.constant.PageConstant;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 元数据属性数据传输对象
 */
@Data
public class RdeRiskVariableRecordDTO {

    private Integer id;

    /**
     * 标识
     */
    @NotBlank(message = "属性code不能为空")
    @Size(min = 1, max = 30, message = "属性code不能超过30个字符")
    private String code;

    /**
     * 名称
     */
    @NotBlank(message = "属性名不能为空")
    @Size(min = 1, max = 30, message = "属性名不能超过30个字符")
    private String name;

    /**
     * 类型
     */
    @NotBlank(message = "属性类型不能为空")
    private String type;

    /**
     * 备注
     */
    @Size(min = 1, max = 200, message = "属性备注不能超过200个字符")
    private String remark;

    /**
     * 对象分组序号
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "groupNo不合法")
    private String groupNo;

    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "recordNo不合法")
    private String recordNo;

    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "parentNo不合法")
    private String parentNo;

    /**
     * 关联的接口参数
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceFieldIdManage 不合法")
    private String interfaceFieldIdManage;

    /**
     * 关联的接口
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "interfaceManageNo 不合法")
    private String interfaceManageNo;

    @Min(value = 1, message = "pageNum不能小于1")
    private Long pageNum = PageConstant.pageNum;

    @Max(value = 30, message = "pageSize不能大于30")
    @Min(value = 1, message = "pageSize不能小于1")
    private Long pageSize = PageConstant.pageSize;
}
