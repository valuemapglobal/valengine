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
 * @author vlauemap team
 * @since 2026/01/22
 */
public class HeaderInterceptor implements AsyncHandlerInterceptor
{
    private static final Logger log = LoggerFactory.getLogger(HeaderInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (request == null)
        {
            return true;
        }
        if (!(handler instanceof HandlerMethod))
        {
            return true;
        }

        SecurityContextHolder.setUserId(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USER_ID));
        SecurityContextHolder.setUserName(ServletUtils.getHeader(request, SecurityConstants.DETAILS_USERNAME));
        SecurityContextHolder.setUserKey(ServletUtils.getHeader(request, SecurityConstants.USER_KEY));

        String token = SecurityUtils.getToken(request);

        if (StringUtils.isNotEmpty(token))
        {
            LoginUser loginUser = AuthUtil.getLoginUser(token);
            if (StringUtils.isNotNull(loginUser))
            {
                AuthUtil.verifyLoginUserExpire(loginUser);
                SecurityContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
                // 从 LoginUser 中设置用户信息到 SecurityContextHolder
                if (loginUser.getUserid() != null)
                {
                    SecurityContextHolder.setUserId(String.valueOf(loginUser.getUserid()));
                }
                if (StringUtils.isNotEmpty(loginUser.getUsername()))
                {
                    SecurityContextHolder.setUserName(loginUser.getUsername());
                }
                if (StringUtils.isNotEmpty(loginUser.getToken()))
                {
                    SecurityContextHolder.setUserKey(loginUser.getToken());
                }
            }
            else
            {
                log.debug("HeaderInterceptor - loginUser not found for token");
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
