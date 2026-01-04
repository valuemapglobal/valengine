package com.value.decision.framework.aspectj.lang.annotation;

import java.lang.annotation.*;

/**
 * @author Vida
 * @date 2024年11月08日 17:49
 * @description 被标记的方法会进行登录验证。
 * 并且如果方法参数含有com.risksmart.system.api.model.LoginUser类型的参数会被自动注入
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirLoginUser {
    String value() default "";
}
