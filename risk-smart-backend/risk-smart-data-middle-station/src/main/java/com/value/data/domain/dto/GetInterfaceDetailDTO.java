package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

/**
 * @author Vida
 * @date 2023年11月15日 16:12
 * @description
 */
@Data
public class GetInterfaceDetailDTO {
    @NotEmpty(message = "sourceNo 不能为空")
    private String sourceNo;
    @NotEmpty(message = "manageNo 不能为空")
    private String manageNo;
}
