package com.value.decision.model.decisionmanage.model.dto;

import com.value.decision.model.decisionmanage.model.Business;
import lombok.Data;

/**
 * 业务场景查询DTO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class BusinessQueryDTO extends Business {
    private Integer pageNum;
    private Integer pageSize;
}
