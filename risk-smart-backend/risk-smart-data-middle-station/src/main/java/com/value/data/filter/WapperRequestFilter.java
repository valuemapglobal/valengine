package com.value.data.filter;


import com.value.data.config.RequestWrapper;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*", filterName = "wapperRequestFilter")
public class WapperRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 如果是文件上传，不执行这个过滤
        String contentType = servletRequest.getContentType();
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        ServletRequest requestWrapper = null;
        if(servletRequest instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        }
        if(requestWrapper == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // 将请求封装并传递下去
            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
