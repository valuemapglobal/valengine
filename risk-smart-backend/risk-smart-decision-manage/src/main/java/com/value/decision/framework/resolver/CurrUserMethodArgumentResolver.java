package com.value.decision.framework.resolver;

import com.value.decision.common.annotation.CurrUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @descr  实现@currUser的参数解析器
 * @author Raysen
 * @create 2021/1/13 17:34
 **/
public class CurrUserMethodArgumentResolver implements HandlerMethodArgumentResolver {


    /**
     * @descr  判断什么时候要执行下面的resolveArgument方法。判断当一个方法的参数含有@CurrUser的时候返回true。
     * @author Raysen
     * @create 2021/1/13 17:34
     **/
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        if(methodParameter.hasParameterAnnotation(CurrUser.class)){
            return true;
        }
        return false;
    }

    /**
     * @descr  直接把放在session中的用户信息放回去即可
     * @author Raysen
     * @create 2021/1/13 17:35
     **/
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        CurrUser currentUserAnnotation = methodParameter.getParameterAnnotation(CurrUser.class);
        return nativeWebRequest.getAttribute(currentUserAnnotation.value(), NativeWebRequest.SCOPE_SESSION);
    }
}
