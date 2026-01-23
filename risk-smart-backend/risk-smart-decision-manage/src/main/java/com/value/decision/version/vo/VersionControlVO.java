package com.value.decision.version.vo;

import lombok.Data;

@Data
public class VersionControlVO {

    /**
     * 模型id
     */
    private Integer modelId;

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

    //------为了版本号拼接------

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 左侧产品导航 产品名称
     */
    private String projectName;

    /**
     * 业务场景导航 业务名称
     */
    private String businessName;

    /**
     * 区分企业和个人 P个人C企业
     */
    private String personOrCompany;

    /**
     * 此模型是否为冠军标识 1是 0为挑战者版本
     */
    private Integer championVersion;


    //****************************

    /**
     * 此模型当前版本号
     */
    private String versionControl;

    /**
     * 部门ID
     */
    private Integer deptId;
}
