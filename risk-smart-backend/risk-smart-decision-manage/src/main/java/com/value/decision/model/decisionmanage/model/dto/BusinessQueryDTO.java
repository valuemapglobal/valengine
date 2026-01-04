package com.value.decision.model.decisionmanage.model.dto;

import com.value.decision.model.decisionmanage.model.Business;
import lombok.Data;

/**
 * @author Vida
 * @date 2024年11月08日 16:02
 * @description
 */
@Data
public class BusinessQueryDTO extends Business {
    private Integer pageNum;
    private Integer pageSize;
}
