package com.value.decision.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsConfig
{
    /** 项目名称 */
    private static String url;

    private static String diffUrl;

    /** 版本 */
    private static String appKey;

    /** 版权年份 */
    private static String appSecret;

    private static String sender;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        SmsConfig.url = url;
    }

    public static String getDiffUrl() {
        return diffUrl;
    }

    public static void setDiffUrl(String diffUrl) {
        SmsConfig.diffUrl = diffUrl;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static void setAppKey(String appKey) {
        SmsConfig.appKey = appKey;
    }

    public static String getAppSecret() {
        return appSecret;
    }

    public static void setAppSecret(String appSecret) {
        SmsConfig.appSecret = appSecret;
    }

    public static String getSender() {
        return sender;
    }

    public static void setSender(String sender) {
        SmsConfig.sender = sender;
    }
}