package com.value.decision.common.constraint;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Vida
 * @date 2025年03月12日 15:01
 * @description
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
