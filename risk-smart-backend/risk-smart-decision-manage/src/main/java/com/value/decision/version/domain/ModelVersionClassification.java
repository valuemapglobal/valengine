package com.value.decision.version.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Date;

/**
 * 版本归类冠军标识表
 */

@Data
public class ModelVersionClassification {

    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型id
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 模型名称
     */
    @Column(name = "model_name")
    private String modelName;

    /**
     * 版本控制 版本号
     */
    @Column(name = "version_control")
    private String versionControl;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Integer dataStatus;

    /**
     * 是否为冠军版本标识 1是 0为挑战者版本
     */
    @Column(name = "champion_version")
    private Integer championVersion;

    /**
     * 是否为决策平台展示版本号 1是 0不是
     */
    @Column(name = "new_version")
    private Integer newVersion;

    /**
     * 当前模型使用版本号
     */
    @Column(name = "user_version")
    private Integer userVersion;

    /**
     * 部门ID
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 产品
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    @Column(name = "rule_code")
    private String ruleCode;

}
