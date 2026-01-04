package com.value.decision.common.constant;

/**
 * 权限相关通用常量
 * 
 * @author ruoyi
 */
public class SecurityConstants
{
    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * 请求来源
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    /**
     * 用户标识
     */
    public static final String USER_KEY = "user_key";

    /**
     * 登录用户
     */
    public static final String LOGIN_USER = "login_user";

    public static  final String ERR_SECURITY_MSG="操作失败：登录失效";

    public static  final String USER_INFORMATION_SPACE="操作失败：用户信息为空";
    public static final int ERR_SECURITY_CODE = 600;
}
