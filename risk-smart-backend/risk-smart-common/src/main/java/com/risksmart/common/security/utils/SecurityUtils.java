package com.risksmart.common.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.constant.TokenConstants;
import com.risksmart.common.core.context.SecurityContextHolder;
import com.risksmart.common.core.utils.ServletUtils;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.system.api.model.LoginUser;

/**
 * 权限获取工具类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class SecurityUtils
{
    private static final Long ADMIN_ID = 1L;
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 获取用户ID
     */
    public static Long getUserId()
    {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername()
    {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey()
    {
        return SecurityContextHolder.getUserKey();
    }

    /**
     * 获取登录用户信息
     */
    public static LoginUser getLoginUser()
    {
        return SecurityContextHolder.get(SecurityConstants.LOGIN_USER, LoginUser.class);
    }

    /**
     * 获取请求token
     */
    public static String getToken()
    {
        return getToken(ServletUtils.getRequest());
    }

    /**
     * 根据request获取请求token
     */
    public static String getToken(HttpServletRequest request)
    {
        if (request == null)
        {
            return null;
        }
        // 从header获取token标识
        String token = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        return replaceTokenPrefix(token);
    }

    /**
     * 裁剪token前缀
     */
    public static String replaceTokenPrefix(String token)
    {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (StringUtils.isEmpty(token))
        {
            return token;
        }
        if (token.startsWith(TokenConstants.PREFIX))
        {
            token = token.substring(TokenConstants.PREFIX.length());
        }
        return token;
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return ADMIN_ID.equals(userId);
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
