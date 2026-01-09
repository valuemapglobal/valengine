package com.value.decision.common.utils.security;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.value.decision.common.constant.CacheConstants;
import com.value.decision.common.constant.SecurityConstants;
import com.value.decision.common.constant.TokenConstants;
import com.risksmart.common.core.exception.ServiceException;
import com.value.decision.common.utils.JwtUtils;
import com.value.decision.common.utils.StringUtils;
import com.value.decision.framework.redis.RedisCache;
import com.risksmart.system.domain.SysUser;
import com.value.decision.common.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统安全工具类
 * @author Raysen
 * @create 2021/12/2 17:03
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final RedisCache redisCache;
    private final BusinessProperties businessProperties;

    private static SecurityUtils instance;

    @jakarta.annotation.PostConstruct
    private void init() {
        instance = this;
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        HttpServletRequest request = getRequest();
        return getLoginUser(request);
    }

    /**
     * 获取登录用户身份
     */
    public static LoginUser getLoginUser(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.AUTHENTICATION);

        if (StringUtils.isEmpty(token)) {
            return parseOpenApiToken(request);
        }

        return parseJwtToken(token);
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ServiceException("无法获取当前请求上下文", SecurityConstants.ERR_SECURITY_CODE);
        }
        return attributes.getRequest();
    }

    /**
     * 解析 OpenAPI Token
     */
    private static LoginUser parseOpenApiToken(HttpServletRequest request) {
        String openApiToken = request.getHeader("open-Api-Token");
        if (StringUtils.isEmpty(openApiToken)) {
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG, SecurityConstants.ERR_SECURITY_CODE);
        }

        try {
            String userInfoJson = AESUtil.decryptFromBase64String(openApiToken, instance.businessProperties.getApiTokenDecryptPassword());
            JSONObject userInfo = JSONObject.parseObject(userInfoJson);
            return buildLoginUser(
                    userInfo.getLongValue("userId"),
                    userInfo.getString("userName"),
                    userInfo.getLongValue("deptId")
            );
        } catch (Exception e) {
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG, SecurityConstants.ERR_SECURITY_CODE);
        }
    }

    /**
     * 解析 JWT Token 并从 Redis 获取用户信息
     */
    private static LoginUser parseJwtToken(String token) {
        String userKey = JwtUtils.getUserKey(token);
        String redisKey = CacheConstants.LOGIN_TOKEN_KEY + userKey;
        com.risksmart.system.api.model.LoginUser tempLoginUser = instance.redisCache.getCacheObject(redisKey);

        if (tempLoginUser == null) {
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG, 600);
        }

        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(tempLoginUser, loginUser);
        return loginUser;
    }

    /**
     * 构建 LoginUser 对象
     */
    private static LoginUser buildLoginUser(Long userId, String userName, Long deptId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setUserName(userName);
        if (deptId != null) {
            sysUser.setDeptId(deptId);
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setSysUser(sysUser);
        loginUser.setUserid(userId);
        loginUser.setUsername(userName);
        return loginUser;
    }
}
