package com.value.decision.model.decisionmanage.model.dto.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class ModelRuleResultDTO {

    /**
     * 评分数据
     */
    private JSONObject scoreData;

    /**
     * 规则数据
     */
    private JSONObject ruleData;

    /**
     * 分类数据
     */
    private JSONObject sortData;

    /**
     * 评级数据
     */
    private JSONObject rateData;

    /**
     * 额度数据
     */
    private JSONObject quotaData;

    /**
     * 定价数据
     */
    private JSONObject priceData;
}
