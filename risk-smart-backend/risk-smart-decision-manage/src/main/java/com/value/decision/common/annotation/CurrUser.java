package com.value.decision.common.annotation;


import com.value.decision.common.constant.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @descr  小程序当前登录用户
 * @author Raysen
 * @create 2021/1/13 17:29
 **/
@Target(ElementType.PARAMETER) // 作用到参数上
@Retention(RetentionPolicy.RUNTIME) // 运行时有效
public @interface CurrUser {

    //当前用户在session对象中的key
    String value() default Constants.MINI_USER_SESSION_KEY;

}
