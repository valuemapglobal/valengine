package com.value.decision.engine.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataVO {

    /**
     * 命中codeList
     */
    private List<String> codeList;

    /**
     * 分类数据详情
     */
    private JSONObject jsonObject = new JSONObject();

    /**
     * 命中预警code以及详情id
     */
    private List<WarnVO> warnVOList;

}
