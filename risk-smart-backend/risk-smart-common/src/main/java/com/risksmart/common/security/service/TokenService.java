package com.risksmart.common.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.risksmart.common.core.constant.CacheConstants;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.utils.JwtUtils;
import com.risksmart.common.core.utils.ServletUtils;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.utils.ip.IpUtils;
import com.risksmart.common.core.utils.uuid.IdUtils;
import com.risksmart.common.redis.service.RedisService;
import com.risksmart.common.security.utils.SecurityUtils;
import com.risksmart.system.api.model.LoginUser;

/**
 * Token验证处理
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Component
public class TokenService
{
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private RedisService redisService;

    private static final long MILLIS_SECOND = 1000;

    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final long TOKEN_EXPIRE_TIME = CacheConstants.EXPIRATION;

    private static final String ACCESS_TOKEN = CacheConstants.LOGIN_TOKEN_KEY;

    private static final long TOKEN_REFRESH_THRESHOLD_MILLIS = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;

    /**
     * 创建令牌
     */
    public Map<String, Object> createToken(LoginUser loginUser)
    {
        if (loginUser == null || loginUser.getSysUser() == null)
        {
            return new HashMap<>(0);
        }
        String token = IdUtils.fastUUID();
        Long userId = loginUser.getSysUser().getUserId();
        String userName = loginUser.getSysUser().getUserName();
        loginUser.setToken(token);
        loginUser.setUserid(userId);
        loginUser.setUsername(userName);
        loginUser.setIpaddr(IpUtils.getIpAddr());
        refreshToken(loginUser);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<>(4);
        claimsMap.put(SecurityConstants.USER_KEY, token);
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, userId);
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, userName);

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<>(2);
        rspMap.put("access_token", JwtUtils.createToken(claimsMap));
        rspMap.put("expires_in", TOKEN_EXPIRE_TIME);
        return rspMap;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser()
    {
        return getLoginUser(ServletUtils.getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = request == null ? null : SecurityUtils.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token)
    {
        LoginUser user = null;
        try
        {
            if (StringUtils.isNotEmpty(token))
            {
                String userkey = JwtUtils.getUserKey(token);
                String redisKey = getTokenKey(userkey);
                user = redisService.getCacheObject(redisKey);
                return user;
            }
        }
        catch (Exception e)
        {
            log.error("获取用户信息异常: {}", e.getMessage(), e);
        }
        return user;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userkey = JwtUtils.getUserKey(token);
            redisService.deleteObject(getTokenKey(userkey));
        }
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param loginUser
     */
    public void verifyToken(LoginUser loginUser)
    {
        if (loginUser == null)
        {
            return;
        }
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= TOKEN_REFRESH_THRESHOLD_MILLIS)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        if (loginUser == null || StringUtils.isEmpty(loginUser.getToken()))
        {
            return;
        }
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + TOKEN_EXPIRE_TIME * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisService.setCacheObject(userKey, loginUser, TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    private String getTokenKey(String token)
    {
        return ACCESS_TOKEN + token;
    }
}
