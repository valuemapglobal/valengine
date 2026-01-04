package com.value.decision.model.decisionmanage.model.dto;

import com.value.decision.model.decisionmanage.model.Product;
import lombok.Data;

/**
 * @author Vida
 * @date 2024年11月08日 16:39
 * @description
 */
@Data
public class ProductQueryDTO extends Product {
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
