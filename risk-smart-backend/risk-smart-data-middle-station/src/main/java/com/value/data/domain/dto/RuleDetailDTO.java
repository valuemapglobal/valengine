package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

/**
 * 规则详情数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class RuleDetailDTO implements Serializable {
    @NotBlank(message = "packageNo 不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "packageNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String packageNo;
    @NotBlank(message = "objectNo 不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "objectNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String objectNo;
    @NotBlank(message = "attributeNo 不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "attributeNo 只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String attributeNo;
    @NotBlank(message = "operator 不能为空")
    private String operator;
    @NotBlank(message = "value 不能为空")
    private String value;
    private List<RuleDetailDTO> condition;
}
