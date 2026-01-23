package com.value.decision.common.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用工具类
 * @author Raysen
 * @create 2020/10/14 9:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUtil {

    private final FeishuProperties feishuProperties;

    private static CommonUtil instance;

    @jakarta.annotation.PostConstruct
    private void init() {
        instance = this;
    }

    /**
     * 飞书发送群消息
     */
    public static boolean sendBotMessage(String message) {
        if (instance == null || !instance.feishuProperties.isConfigured()) {
            return false;
        }

        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("groupUrl", instance.feishuProperties.getFeishuBusinessGroupUrl());
            paramMap.put("message", "决策系统异常_\n" + message);

            String res = HttpUtil.post(instance.feishuProperties.getIntranetPushUrl(), JSON.toJSONString(paramMap));
            JSONObject jsonObject = JSONObject.parseObject(res);

            return jsonObject != null && jsonObject.getIntValue("code") == 200;
        } catch (Exception e) {
            log.error("发送飞书消息失败", e);
            return false;
        }
    }

    /**
     * 飞书配置属性
     */
    @lombok.Data
    @Component
    @ConfigurationProperties(prefix = "business")
    public static class FeishuProperties {

        private String feishuBusinessGroupUrl;

        private String intranetPushUrl;

        public boolean isConfigured() {
            return feishuBusinessGroupUrl != null && !feishuBusinessGroupUrl.isEmpty()
                    && intranetPushUrl != null && !intranetPushUrl.isEmpty();
        }
    }
}
