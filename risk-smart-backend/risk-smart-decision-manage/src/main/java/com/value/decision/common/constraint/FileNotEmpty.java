package com.value.decision.common.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 文件非空校验注解
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Documented
@Constraint(validatedBy = {FileNotEmptyValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileNotEmpty {
    String message() default "上传的文件不能为空";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
