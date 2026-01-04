package com.value.decision.model.decisionmanage.model.dto.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class ModelRuleDTO {

    /**
     * 请求决策接口数据
     */
    private JSONObject requestData;

    /**
     * drl脚本
     */
    private List<String> drlList;

    /**
     * 数据响应形式
     */
    private JSONObject responseData;
}
