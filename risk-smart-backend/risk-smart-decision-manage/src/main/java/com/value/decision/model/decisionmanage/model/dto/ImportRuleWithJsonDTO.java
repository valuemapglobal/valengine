package com.value.decision.model.decisionmanage.model.dto;

import com.value.decision.common.constraint.FileNotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author Vida
 * @date 2025年03月12日 14:50
 * @description
 */
@Data
public class ImportRuleWithJsonDTO {
    @FileNotEmpty
    private MultipartFile file;
    @NotNull(message = "productId 不能为空")
    private Integer productId;
    @NotNull(message = "businessId 不能为空")
    private Long businessId;
    @NotBlank(message = "modelType 不能为空")
    private String modelType;
}
