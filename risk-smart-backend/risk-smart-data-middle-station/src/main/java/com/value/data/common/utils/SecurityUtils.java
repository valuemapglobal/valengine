package com.value.data.common.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.risksmart.common.core.config.BusinessProperties;
import com.risksmart.common.core.constant.CacheConstants;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.constant.TokenConstants;
import com.risksmart.common.core.exception.ServiceException;
import com.risksmart.common.core.utils.JwtUtils;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.utils.sign.AESUtil;
import com.value.data.common.model.LoginUser;
import com.value.data.common.model.SysUser;
import com.value.data.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统安全工具类
 * @author C3B6
 * @create 2026/01/09 14:38
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final RedisService redisService;
    private final BusinessProperties businessProperties;

    private static SecurityUtils instance;

    @jakarta.annotation.PostConstruct
    private void init() {
        instance = this;
    }

    /**
     * 获取当前登录用户（从当前请求上下文中获取）
     */
    public static LoginUser getLoginUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new ServiceException("无法获取当前请求上下文", SecurityConstants.ERR_SECURITY_CODE);
        }
        return getLoginUser(attributes.getRequest());
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
        Object cacheObject = instance.redisService.getCacheObject(redisKey);

        if (cacheObject == null) {
            throw new ServiceException(SecurityConstants.ERR_SECURITY_MSG, 600);
        }

        return convertToLoginUser(cacheObject);
    }

    /**
     * 将缓存对象转换为 LoginUser
     */
    private static LoginUser convertToLoginUser(Object cacheObject) {
        if (cacheObject instanceof JSONObject jsonObject) {
            Long userId = jsonObject.getLong("userid");
            String userName = jsonObject.getString("username");
            Long deptId = null;

            JSONObject sysUserJson = jsonObject.getJSONObject("sysUser");
            if (sysUserJson != null) {
                deptId = sysUserJson.getLong("deptId");
            }

            return buildLoginUser(userId, userName, deptId);
        }

        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(cacheObject, loginUser);
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
