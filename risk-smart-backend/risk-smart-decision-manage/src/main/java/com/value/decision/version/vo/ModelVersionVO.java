package com.value.decision.version.vo;

import lombok.Data;

@Data
public class ModelVersionVO {

    /**
     * 模型ID
     */
    private Integer modelId;

    /**
     * 冠军版本
     */
    private String championVersion;

    /**
     * 当前模型使用版本号
     */
    private String userVersion;

    /**
     * 当前模型展示版本号
     */
    private String newVersion;

    /**
     * 策略类型导航标识
     */
    private String ruleCode;

    /**
     * 业务场景导航标识
     */
    private String businessCode;

    /**
     * 左侧产品导航标识
     */
    private String projectCode;

    /**
     * token
     */
    private String authorization;
}
