package com.value.decision.framework.aspectj.lang.annotation;

import java.lang.annotation.*;

/**
 * 登录用户验证注解
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirLoginUser {
    String value() default "";
}
