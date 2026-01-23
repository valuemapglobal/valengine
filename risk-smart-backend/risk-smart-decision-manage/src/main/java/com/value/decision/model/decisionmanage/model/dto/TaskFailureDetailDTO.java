package com.value.decision.model.decisionmanage.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务失败详情DTO
 *
 * @author Austin
 * @since 2025-01-16
 */
@Data
public class TaskFailureDetailDTO {

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 流程策略ID
     */
    private Integer processId;

    /**
     * 流程策略名称
     */
    private String processStrategy;

    /**
     * 失败时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime failureTime;

    /**
     * 场景编码 (A1-F9)
     */
    private String sceneCode;

    /**
     * 失败大类 (用户可见)
     */
    private String failureCategory;

    /**
     * 失败场景 (开发可见)
     */
    private String failureScene;

    /**
     * 用户友好提示消息
     */
    private String userMessage;

    /**
     * 是否可重试 (0-否, 1-是)
     */
    private Integer isRetryable;

    /**
     * 重试建议
     */
    private String retryTips;

    /**
     * 技术详情 (可选,给技术支持查看)
     */
    private Map<String, Object> technicalDetail;
}
