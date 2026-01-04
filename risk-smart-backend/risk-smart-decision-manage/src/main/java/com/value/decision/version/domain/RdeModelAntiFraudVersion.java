package com.value.decision.version.domain;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 反欺诈模型表
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
@Data
@Table(name = "rde_model_anti_fraud_version")
public class RdeModelAntiFraudVersion {

//    @Id
//    @GeneratedValue(generator = "JDBC")
    @Column(name = "id")
    private Integer id;

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
     * 模型类型：1.银行流水,2.kyc报告，3风险预警。。。
     */
    @Column(name = "model_type")
    private String modelType;

    /**
     * 状态1启用 0禁用
     */
    @Column(name = "status")
    private String status;

    /**
     * 验证状态（0未验证，1已验证，2验证失败）
     */
    @Column(name = "check_status")
    private Integer checkStatus;

    /**
     * 新规则
     */
    @Column(name = "new_rule")
    private String newRule;

    /**
     * 运行规则
     */
    @Column(name = "run_rule")
    private String runRule;

    /**
     * 规则类型（	1反欺诈模型 	2申请模型	3准入模型 	4黑灰名单模型 	5评分模型 	6财务测算模型	7额度测算模型 	8风险定价模型 	9贷后监控模型 	10违约预测模型 	11宏观预测模型 	12行业预测模型 	13地域风险模型）-AI风险预警新定义类型（1自身风险，2法人风险，3舆情风险，4关联风险）
     */
    @Column(name = "rule_type")
    private Integer ruleType;

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
     * 公司id
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 数据状态 0是正常，1删除
     */
    @Column(name = "data_status")
    private Integer dataStatus;

    /**
     * 部门标识101为1，其他为2
     */
    @Column(name = "dept_flag")
    private String deptFlag;

    /**
     * 页面标识 分类为1,规则为0
     */
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "business_code")
    private String businessCode;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;
}
