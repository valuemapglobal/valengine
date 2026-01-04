package com.value.decision.model.decisionmanage.model.dto.model;


import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 模型测试列表返回
 */
@Data
public class ModelTestTaskDTO {

    /**
     * 序号
     */
    private Integer id;

    /**
     * 测试任务号
     */
    private String taskNo;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型描述
     */
    private String modelRemark;

    /**
     * 模型版本号
     */
    private String modelVerson;

    /**
     * 业务场景 例如1准入 3预授信
     */
    private String businessName;

    /**
     * 策略场景标识  例如1评分模型 5规则模型 6分类模型
     */
    private String ruleName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 测试状态
     */
    private Integer testStatus;
}
