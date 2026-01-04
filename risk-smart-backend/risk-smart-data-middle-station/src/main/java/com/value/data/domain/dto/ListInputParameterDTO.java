package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @author Vida
 * @date 2024年01月30日 17:44
 * @description
 */
@Data
public class ListInputParameterDTO {
    @NotBlank(message = "manageNo不能为空")
    private String manageNo;
}
