package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 定价矩阵行DTO
 */
@Data
public class PriceMatrixRowDTO {

    /**
     * 评级（如：AAA、AA、A、BBB、BB、B、C、D、E）
     */
    private String grade;

    /**
     * 定价数组，对应各个额度区间的定价（如：["5.0", "4.5", "4.0", "3.5"]）
     */
    private List<String> prices;

    /**
     * 该评级的说明描述
     */
    private String description;
}
