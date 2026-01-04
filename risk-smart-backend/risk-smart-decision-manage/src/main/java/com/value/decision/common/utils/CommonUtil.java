package com.value.decision.common.utils;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * @descr  通用工具类
 * @author Raysen
 * @create 2020/10/14 9:09
 **/
@Component
public class CommonUtil {

    //静态初始化当前类
    private static CommonUtil commonUtil;

    @PostConstruct
    public void init(){
        //声明的静态类=this
        commonUtil = this;
    }

    @Value("${business.feishuBusinessGroupUrl:}")
    private String feishuBusinessGroupUrl;

    @Value("${business.intranetPushUrl:}")
    private String intranetPushUrl;

    /**
     * 飞书发送群消息
     */
    public static boolean sendBotMessage(String message){
        if (commonUtil == null || commonUtil.feishuBusinessGroupUrl == null || commonUtil.feishuBusinessGroupUrl.isEmpty()
            || commonUtil.intranetPushUrl == null || commonUtil.intranetPushUrl.isEmpty()) {
            return false;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("groupUrl", commonUtil.feishuBusinessGroupUrl);
        paramMap.put("message", "决策系统异常_" + "\n" +message);
        String res = HttpUtil.post(commonUtil.intranetPushUrl, JSON.toJSONString(paramMap));

        JSONObject jsonObject = JSONObject.parseObject(res);
        if(jsonObject == null){
            return false;
        }
        if(jsonObject.getIntValue("code")!=200){
            return false;
        }else{
            return true;
        }
    }

}
