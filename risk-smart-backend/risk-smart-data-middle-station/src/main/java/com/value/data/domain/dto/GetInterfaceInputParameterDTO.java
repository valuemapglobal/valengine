package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 获取接口入参参数数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class GetInterfaceInputParameterDTO {
    @NotEmpty(message = "manageNoList 不能为空")
    private List<String> manageNoList;
}
