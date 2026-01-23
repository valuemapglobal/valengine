package com.value.decision.engine.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class JsonRuleEntityVO {

    /**
     * 任务编号
     */
    private String number;

    /**
     * 数据
     */
    private JSONObject JsonObject;

    /**
     * 规则
     */
    private List<String> strList;


}
