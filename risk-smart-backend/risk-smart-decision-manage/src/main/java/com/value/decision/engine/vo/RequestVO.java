package com.value.decision.engine.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class RequestVO {

    //规则文件
    private List<String> ruleStr;

    //数据
    private JSONObject jsonObject;
}
