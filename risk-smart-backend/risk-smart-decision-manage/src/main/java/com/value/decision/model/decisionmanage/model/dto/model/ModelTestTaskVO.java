package com.value.decision.model.decisionmanage.model.dto.model;


import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 模型测试入参
 */
@Data
public class ModelTestTaskVO {

    /**
     * 模型id
     */
    @NotNull(message = "模型id不可为空")
    private Integer modelId;

    /**
     * 产品名称
     */
    @NotNull(message = "产品名称不可为空")
    private String productName;

    /**
     * 业务场景 例如1准入 3预授信
     */
    @NotNull(message = "业务场景标识不可为空")
    private Integer businessCode;

    /**
     * 策略场景标识  例如1评分模型 5规则模型 6分类模型
     */
    @NotNull(message = "策略场景标识不可为空")
    private Integer ruleCode;

    /**
     * 模型名称
     */
    @NotNull(message = "模型名称不可为空")
    private String modelName;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 产品编号
     */
    private Integer productCode;

    /**
     * 模型描述
     */
    private String modelRemark;

    /**
     * 业务场景名称
     */
    private String businessName;

    /**
     * 测试状态
     */
    private Integer testStatus;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    private Integer pageNum;
    private Integer pageSize;
}
