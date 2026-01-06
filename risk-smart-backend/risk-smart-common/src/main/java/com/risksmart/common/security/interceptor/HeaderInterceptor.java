package com.risksmart.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.context.SecurityContextHolder;
import com.risksmart.common.core.utils.ServletUtils;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.security.auth.AuthUtil;
import com.risksmart.common.security.utils.SecurityUtils;
import com.risksmart.system.api.model.LoginUser;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 * 注意：此拦截器会同时验证当前用户有效期自动刷新有效期
 *
 * @author ruoyi
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor
{
    private static final Logger log = LoggerFactory.getLogger(HeaderInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }

        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));

        String token = SecurityUtils.getToken();
        log.debug("HeaderInterceptor - token: {}", token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null");

        if (StringUtils.isNotEmpty(token))
        {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            log.debug("HeaderInterceptor - loginUser: {}, userid: {}",
                loginUser != null ? "found" : "null",
                loginUser != null ? loginUser.getUserid() : "N/A");

            if (StringUtils.isNotNull(loginUser))
            {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                // 从 LoginUser 中设置用户信息到 SecurityContextHolder
                if (loginUser.getUserid() != null) {
                    SecurityContextHolder.setUserId(String.valueOf(loginUser.getUserid()));
                    log.debug("HeaderInterceptor - set userId to: {}", loginUser.getUserid());
                }
                if (StringUtils.isNotEmpty(loginUser.getUsername())) {
                    SecurityContextHolder.setUserName(loginUser.getUsername());
                }
                if (StringUtils.isNotEmpty(loginUser.getToken())) {
                    SecurityContextHolder.setUserKey(loginUser.getToken());
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception
    {
        SecurityContextHolder.remove();
    }
}
