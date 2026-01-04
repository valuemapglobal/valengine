package com.value.decision.model.decisionmanage.model.dto.batch;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 提交批次任务请求DTO
 *
 * @author Claude
 * @since 2025-01-14
 */
@Data
public class SubmitBatchDTO {

    /**
     * 文件URL
     */
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    /**
     * 流程策略ID
     */
    @NotNull(message = "流程策略ID不能为空")
    private Integer processId;

    /**
     * 响应形式 1数据 2报告
     */
    private Integer responseForm = 1;
}