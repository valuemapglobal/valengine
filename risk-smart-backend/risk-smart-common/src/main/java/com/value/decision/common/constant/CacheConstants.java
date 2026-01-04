package com.value.decision.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author ruoyi
 */
public class CacheConstants
{
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * Redis前缀：流水报告通知
     */
    public final static String REPORT_NOTIFY = "vm-risk-value:report_notify:";

    /**
     * Redis前缀：用户key下载次数记录
     */
    public final static String KYC_DOWNLOAD_COUNT = "vm-risk-value:kyc_download_count:";

    /**
     * Redis前缀：KYC下载请求
     */
    public final static String KYC_DOWNLOAD_REQUEST = "vm-risk-value:kyc_download_request:";
}
