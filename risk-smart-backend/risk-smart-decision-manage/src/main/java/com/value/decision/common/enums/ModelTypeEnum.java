package com.value.decision.common.enums;

import lombok.Data;

/**
 * @author Vida
 * @date 2024年11月21日 15:00
 * @description
 */
public enum ModelTypeEnum {
    SCORE(1, "评分模型"),
    LEVEL(2,"评级模型"),
    CREDIT_LIMIT(3,"额度模型"),
    PRICING(4,"定价模型"),
    RULE(5,"规则模型"),
    CLASSIFICATION(6,"分类模型");
    private Integer modelType;
    private String desc;
    ModelTypeEnum(Integer modelType, String desc) {
        this.modelType = modelType;
        this.desc = desc;
    }

    public Integer getModelType() {
        return modelType;
    }

    public String getDesc() {
        return desc;
    }
}
