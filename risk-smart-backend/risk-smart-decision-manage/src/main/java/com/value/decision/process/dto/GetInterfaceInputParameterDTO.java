package com.value.decision.process.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Vida
 * @date 2023年12月19日 10:03
 * @description
 */
@Data
public class GetInterfaceInputParameterDTO {
    @NotEmpty(message = "manageNoList 不能为空")
    private List<String> manageNoList;
}
