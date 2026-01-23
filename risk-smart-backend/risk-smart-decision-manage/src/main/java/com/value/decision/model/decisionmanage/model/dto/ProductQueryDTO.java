package com.value.decision.model.decisionmanage.model.dto;

import com.value.decision.model.decisionmanage.model.Product;
import lombok.Data;

/**
 * 产品查询DTO
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class ProductQueryDTO extends Product {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
