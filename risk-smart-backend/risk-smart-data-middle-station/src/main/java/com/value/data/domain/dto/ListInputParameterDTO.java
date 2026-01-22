package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 查询入参参数列表数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class ListInputParameterDTO {
    @NotBlank(message = "manageNo不能为空")
    private String manageNo;
}
