package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 接口查询DTO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@Accessors(chain = true)
public class InterfaceQueryDTO {
    private List<String> interfaceNos;
    private Long deptId;
}
