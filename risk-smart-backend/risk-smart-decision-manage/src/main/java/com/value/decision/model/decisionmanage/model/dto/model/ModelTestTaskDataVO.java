package com.value.decision.model.decisionmanage.model.dto.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class ModelTestTaskDataVO {

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 模型编号
     */
    private Integer modelId;

    /**
     * 模型版本号
     */
    private String modelVerson;

    /**
     * 产品编号
     */
    private Integer projectCode;

    /**
     * 业务场景导航标识
     */
    private Integer businessCode;

    /**
     * 策略类型导航标识
     */
    private Integer ruleCode;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 申请用户名称
     */
    private Integer applyUserName;

    /**
     * 初始化数据
     */
    private JSONObject ruleData;


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
     * drl脚本
     */
    private List<String> strList;
}
