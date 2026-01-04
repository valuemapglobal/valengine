package com.value.data.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取接口信息
 */
@Data
public class FindInterfaceInfoDTO implements Serializable {


    private static final long serialVersionUID=1L;

    @Size(max = 30, message = "interfaceName应该在1-30字符之间")
    private String interfaceName;

    /**
     * 来源唯一标识
     */
    @NotBlank(message = "sourceNo 参数错误")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "sourceNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String sourceNo;

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
     * 接口类型 0个人接口，1 企业接口，2 司法接口
     */
    private Integer interfaceType;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}
