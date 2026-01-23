package com.risksmart.common.security.annotation;

import java.lang.annotation.*;

/**
 * 内部认证注解
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth
{
    /**
     * 是否校验用户信息
     */
    boolean isUser() default false;
}