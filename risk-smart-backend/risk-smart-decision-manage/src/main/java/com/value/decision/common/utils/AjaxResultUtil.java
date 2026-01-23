package com.value.decision.common.utils;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.risksmart.common.core.web.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class AjaxResultUtil {
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final Logger log = LoggerFactory.getLogger(AjaxResultUtil.class);

    public static AjaxResult parse(String jsonObject) {
        Assert.isTrue(JSONUtil.isTypeJSONObject(jsonObject),"无法解析的json字符串："+jsonObject);
        try {
            return jsonMapper.readValue(jsonObject, AjaxResult.class);
        } catch (JsonProcessingException e) {
            log.error("解析json字符串为AjaxResult失败："+jsonObject);
            throw new RuntimeException(e);
        }

    }
}
