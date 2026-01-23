package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 接口查询数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class InterfaceQueryDTO {
    private List<String> interfaceNos;
    private Long deptId;
}
