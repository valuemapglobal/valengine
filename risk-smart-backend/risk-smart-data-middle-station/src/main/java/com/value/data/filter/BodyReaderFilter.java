package com.value.data.filter;

import com.value.data.config.BodyReaderHttpServletRequestWrapper;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

//@WebFilter(filterName = "bodyReaderFilter", urlPatterns = "/*")
public class BodyReaderFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            // 将请求对象包装为 可重复读取流的请求对象。注意：构造好了，但是需要在拦截器中获取
            requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
        }
        if (requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {
        // do nothing

    }
}
