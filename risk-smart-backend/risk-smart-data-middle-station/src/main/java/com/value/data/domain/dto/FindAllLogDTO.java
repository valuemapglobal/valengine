package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * 获取日志信息
 */
@Data
public class FindAllLogDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 接口名称
     */
    @Size(max = 30, message = "interfaceName应在0-30字符之间")
    private String interfaceName;
    /**
     * 供应商标识
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "sourceNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String sourceNo;

    /**
     * 用户部门
     */
    @Size(max = 30, message = "userDept应在0-30字符之间")
    private String userDept;

    /**
     * 用户id
     */
    @Pattern(regexp = "^[0-9]{0,11}$", message = "userId 必须为0-11位数字")
    private String userId;

    /**
     * 状态
     */
    @Pattern(regexp = "^[01]$", message = "state 只允许 0-成功，1-失败")
    private String state;

    /**
     * 开始时间
     */
    @NotBlank(message = "startTime 参数错误")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "日期格式错误")
    private String startTime;

    /**
     * 结束时间
     */
    @NotBlank(message = "endTime 参数错误")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", message = "日期格式错误")
    private String endTime;

    /**
     * 页码
     */
    @NotNull(message = "pageNum 参数错误")
    @Min(value = 1, message = "pageNum 低于最小值 1页")
    private Integer pageNum;

    /**
     * 数量
     */
    @NotNull(message = "pageSize 参数错误")
    @Max(value = 30, message = "pageSize 高于最大值 30条")
    @Min(value = 1, message = "pageSize 低于最小值 1条")
    private Integer pageSize;

    /**
     * 创建者
     */
    private String createBy;
    //接口中文名称
    private String interfaceNameZh;
}
