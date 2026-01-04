package com.value.decision.framework.aspectj;

import com.value.decision.common.exception.ServiceException;
import com.value.decision.model.decisionmanage.controller.ProductController;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vida
 * @date 2024�?1�?8�?17:59
 * @description
 */
@Aspect
@Component
public class RequirLoginUserAspect {
    @Pointcut("@annotation(com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser)")
    public void ponitCut() { }

    @Around("ponitCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            throw new ServiceException("用户未登录");
        }
        final Object[] args = joinPoint.getArgs();
        final List<Object> args2 = Arrays.stream(args).map(p -> (p instanceof LoginUser) ? loginUser : p).collect(Collectors.toList());
        return joinPoint.proceed(args2.toArray());
    }
}
