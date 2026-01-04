package com.value.decision.common.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Vida
 * @date 2025年03月12日 15:01
 * @description
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
