package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Vida
 * @date 2025年03月19日 13:59
 * @description
 */
@Data
@Accessors(chain = true)
public class InterfaceQueryDTO {
    private List<String> interfaceNos;
    private Long deptId;
}
