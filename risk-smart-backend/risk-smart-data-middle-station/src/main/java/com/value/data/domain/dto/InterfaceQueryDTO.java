package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Vida
 * @date 2025年03月19日 11:03
 * @description
 */
@Data
public class InterfaceQueryDTO {
    private List<String> interfaceNos;
    private Long deptId;
}
