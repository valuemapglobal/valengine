package com.value.data.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author Vida
 * @date 2023年08月14日 19:28
 * @description
 */
@Data
public class AddRuleDTO implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 分析指标标识
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsNo;

    /**
     * 分析指标对象标识
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsObjectNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsObjectNo;

    /**
     * 分析指标规则标识
     */
    @Pattern(regexp = "^[a-zA-Z0-9_:]{1,64}$", message = "analysisIndicatorsRuleNo只允许英文、数字、下划线、冒号，且长度不超过64位")
    private String analysisIndicatorsRuleNo;

    /**
     * 规则结果
     */
    @NotBlank(message = "ruleResult 不能为空")
    @Size(min = 1, max = 30, message = "ruleResult 应该在1-30字符之间")
    private String ruleResult;

    /**
     * 规则名称
     */
    @NotBlank(message = "ruleName 不能为空")
    @Size(min = 1, max = 30, message = "ruleName 应该在1-30字符之间")
    private String ruleName;

    /**
     * 描述
     */
    private String remark;

    /**
     * 判断条件
     */
    @NotEmpty
    private List<RuleDetailDTO> condition;

}
