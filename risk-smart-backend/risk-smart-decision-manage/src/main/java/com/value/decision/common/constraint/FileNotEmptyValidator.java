package com.value.decision.common.constraint;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 文件非空校验器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class FileNotEmptyValidator implements ConstraintValidator<FileNotEmpty, MultipartFile> {

    @Override
    public void initialize(FileNotEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // 如果文件为null或文件大小为0，则认为是无效的
        return file != null && !file.isEmpty();
    }
}
