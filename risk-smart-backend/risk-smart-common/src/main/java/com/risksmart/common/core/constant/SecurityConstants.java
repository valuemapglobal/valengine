package com.risksmart.common.core.constant;

/**
 * 权限相关通用常量
 *
 * @author vlauemap team
 * @since 2026/01/22
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
    public static final String AUTHORIZATION_HEADER = "Authorization";

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

    /**
     * 角色权限
     */
    public static final String ROLE_PERMISSION = "role_permission";


    public static final int ERR_SECURITY_CODE = 600;

    public static final String ERR_SECURITY_MSG = "操作失败：登录失效";

    /**
     * 用户信息为空
     */
    public static final String USER_INFORMATION_SPACE = "用户信息为空";

}
