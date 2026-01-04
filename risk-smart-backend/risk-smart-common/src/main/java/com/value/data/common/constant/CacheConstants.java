package com.value.data.common.constant;

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


    public final static long MONITORING_LIST_CACHE=30;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * Redis前缀：流水报告通知
     */
    public final static String REPORT_NOTIFY = "vm-risk-value:report_notify:";


    /**
     * Redis前缀：个人kyc报告通知
     */
    public final static String REPORT_PERSON_NOTIFY = "vm-risk-value:report_person_notify:";

    /**
     * Redis前缀：用户key下载次数记录
     */
    public final static String KYC_DOWNLOAD_COUNT = "vm-risk-value:kyc_download_count:";

    /**
     * Redis前缀：企业KYC下载请求
     */
    public final static String COMPANY_KYC_DOWNLOAD_REQUEST = "vm-risk-value:company_kyc_download_request:";

    /**
     * Redis前缀：个体kyc下载请求
     */
    public final static String PERSON_KYC_DOWNLOAD_REQUEST = "vm-risk-value:person_kyc_download_request:";

    /*
    * Redis前缀：企业司法下载请求
    * */
    public final static String COMPANY_JUDICIAL_DOWNLOAD_REQUEST = "vm-risk-value:company_judicial_download_request:";
    /*
    * Redis前缀：个人司法下载请求
    * */
    public final static String PERSON_JUDICIAL_DOWNLOAD_REQUEST = "vm-risk-value:person_judicial_download_request:";

    /**
     * Redis前缀：企业监控
     */
    public final static String MONITORY_PRIVATEKEYS = "vm-data-monitoring:monitoring_privateCkeys:";
}
