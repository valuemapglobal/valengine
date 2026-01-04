package com.value.decision.version.domain;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 反欺诈模型规则组表
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
@Data
@Table(name = "rde_model_anti_fraud_rule_group_version")
public class RdeModelAntiFraudRuleGroupVersion {

//    @Id
//    @GeneratedValue(generator = "JDBC")
    @Column(name = "id")
    private Integer id;

    /**
     * 模型ID
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 描述
     */
    @Column(name = "descr")
    private String descr;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 操作人ID
     */
    @Column(name = "create_user_id")
    private Integer createUserId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Integer dataStatus;

    /**
     * 公司id
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 部门标识101为1，其他为2
     */
    @Column(name = "dept_flag")
    private String deptFlag;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "rule_code")
    private String ruleCode;

    @Column(name = "business_code")
    private String businessCode;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;

}
