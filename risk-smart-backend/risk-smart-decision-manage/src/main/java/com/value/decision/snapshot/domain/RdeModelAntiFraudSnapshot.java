package com.value.decision.snapshot.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;

/**
 * 反欺诈模型表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
@Table(name = "rde_model_anti_fraud_snapshot")
public class RdeModelAntiFraudSnapshot {

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
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

    @Column(name = "version_control")
    private String versionControl;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdeModelAntiFraudSnapshot that = (RdeModelAntiFraudSnapshot) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(descr, that.descr) &&
                Objects.equals(modelType, that.modelType) &&
                Objects.equals(status, that.status) &&
                Objects.equals(checkStatus, that.checkStatus) &&
                Objects.equals(newRule, that.newRule) &&
                Objects.equals(runRule, that.runRule) &&
                Objects.equals(ruleType, that.ruleType) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(createUserId, that.createUserId) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(dataStatus, that.dataStatus) &&
                Objects.equals(deptFlag, that.deptFlag) &&
                Objects.equals(ruleCode, that.ruleCode) &&
                Objects.equals(projectCode, that.projectCode) &&
                Objects.equals(businessCode, that.businessCode) &&
                Objects.equals(versionControl, that.versionControl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, descr, modelType, status, checkStatus, newRule, runRule, ruleType, remark, createUserId, createTime, updateTime, deptId, dataStatus, deptFlag, ruleCode, projectCode, businessCode, versionControl);
    }

    @Override
    public String toString() {
        return "RdeModelAntiFraudSnapshot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", modelType='" + modelType + '\'' +
                ", status='" + status + '\'' +
                ", checkStatus=" + checkStatus +
                ", newRule='" + newRule + '\'' +
                ", runRule='" + runRule + '\'' +
                ", ruleType=" + ruleType +
                ", remark='" + remark + '\'' +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deptId=" + deptId +
                ", dataStatus=" + dataStatus +
                ", deptFlag='" + deptFlag + '\'' +
                ", ruleCode='" + ruleCode + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", versionControl='" + versionControl + '\'' +
                '}';
    }
}
