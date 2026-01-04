package com.value.decision.version.domain;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 反欺诈模型规则明细表
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
@Data
@Table(name = "rde_model_anti_fraud_rule_record_version")
public class RdeModelAntiFraudRuleRecordVersion {

//    @Id
//    @GeneratedValue(generator = "JDBC")
    @Column(name = "id")
    private Integer id;

    /**
     * 组ID
     */
    @Column(name = "group_id")
    private Integer groupId;

    /**
     * 规则codeId
     */
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 模型id
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 标识
     */
    @Column(name = "code")
    private String code;

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
     * 控制力度
     */
    @Column(name = "control")
    private String control;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 条件
     */
    @Column(name = "term")
    private String term;

    /**
     * 规则所用的包
     */
    @Column(name = "term_package")
    private String termPackage;

    /**
     * 处理所需别名
     */
    @Column(name = "term_key")
    private String termKey;

    /**
     * 处理所需code
     */
    @Column(name = "term_value")
    private String termValue;

    /**
     * 处理所需其他条件
     */
    @Column(name = "term_other")
    private String termOther;

    /**
     * 生成规则
     */
    @Column(name = "term_rule")
    private String termRule;

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

    /**
     * 版本控制
     */
    @Column(name = "data_module")
    private String dataModule;

}
