package com.value.decision.process.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 获取接口输入参数DTO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class GetInterfaceInputParameterDTO {
    @NotEmpty(message = "manageNoList 不能为空")
    private List<String> manageNoList;
}
