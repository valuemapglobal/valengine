package com.value.decision.model.decisionmanage.model.dto.batch;

import lombok.Data;

/**
 * 数据校验结果
 *
 * @author Claude
 * @since 2025-01-14
 */
@Data
public class ValidateResult {

    /**
     * 是否校验成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建成功结果
     */
    public static ValidateResult success() {
        ValidateResult result = new ValidateResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * 创建失败结果
     */
    public static ValidateResult fail(String errorMessage) {
        ValidateResult result = new ValidateResult();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
