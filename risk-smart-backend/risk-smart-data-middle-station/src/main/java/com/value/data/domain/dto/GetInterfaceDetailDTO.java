package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

/**
 * 获取接口详情数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class GetInterfaceDetailDTO {
    @NotEmpty(message = "sourceNo 不能为空")
    private String sourceNo;
    @NotEmpty(message = "manageNo 不能为空")
    private String manageNo;
}
