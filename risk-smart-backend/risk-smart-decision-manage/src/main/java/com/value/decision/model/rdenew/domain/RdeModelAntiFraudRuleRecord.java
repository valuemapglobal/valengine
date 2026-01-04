package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Objects;

@Table(name = "rde_model_anti_fraud_rule_record")
public class RdeModelAntiFraudRuleRecord extends BaseVO implements Cloneable{
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
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
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 标识
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */

    private String descr;
    /**
     * 控制力度
     */
    private String control;

    /**
     * 状态
     */
    private String status;

    /**
     * 条件
     */
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
     * 数据状态
     */
    @Column(name = "data_status")
    private Byte dataStatus;

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
     * 页面标识 分类为1,规则为0
     */
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;

    @Column(name = "data_module")
    private String dataModule;

    @TableField(exist = false)
    private RdeModelDecisionCodeLevel ruleDetail;

    public RdeModelDecisionCodeLevel getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(RdeModelDecisionCodeLevel ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }


    public String getDeptFlag() {
        return deptFlag;
    }

    public void setDeptFlag(String deptFlag) {
        this.deptFlag = deptFlag;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取组ID
     *
     * @return group_id - 组ID
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * 设置组ID
     *
     * @param groupId 组ID
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取标识
     *
     * @return code - 标识
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置标识
     *
     * @param code 标识
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取描述
     *
     * @return descr - 描述
     */
    public String getDescr() {
        return descr;
    }

    /**
     * 设置描述
     *
     * @param descr 描述
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * 获取控制力度
     *
     * @return control - 控制力度
     */
    public String getControl() {
        return control;
    }

    /**
     * 设置控制力度
     *
     * @param control 控制力度
     */
    public void setControl(String control) {
        this.control = control;
    }

    /**
     * 获取状态
     *
     * @return status - 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取条件
     *
     * @return term - 条件
     */
    public String getTerm() {
        return term;
    }

    /**
     * 设置条件
     *
     * @param term 条件
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * 获取规则所用的包
     *
     * @return term_package - 规则所用的包
     */
    public String getTermPackage() {
        return termPackage;
    }

    /**
     * 设置规则所用的包
     *
     * @param termPackage 规则所用的包
     */
    public void setTermPackage(String termPackage) {
        this.termPackage = termPackage;
    }

    /**
     * 获取处理所需别名
     *
     * @return term_key - 处理所需别名
     */
    public String getTermKey() {
        return termKey;
    }

    /**
     * 设置处理所需别名
     *
     * @param termKey 处理所需别名
     */
    public void setTermKey(String termKey) {
        this.termKey = termKey;
    }

    /**
     * 获取处理所需code
     *
     * @return term_value - 处理所需code
     */
    public String getTermValue() {
        return termValue;
    }

    /**
     * 设置处理所需code
     *
     * @param termValue 处理所需code
     */
    public void setTermValue(String termValue) {
        this.termValue = termValue;
    }

    /**
     * 获取处理所需其他条件
     *
     * @return term_other - 处理所需其他条件
     */
    public String getTermOther() {
        return termOther;
    }

    /**
     * 设置处理所需其他条件
     *
     * @param termOther 处理所需其他条件
     */
    public void setTermOther(String termOther) {
        this.termOther = termOther;
    }

    /**
     * 获取生成规则
     *
     * @return term_rule - 生成规则
     */
    public String getTermRule() {
        return termRule;
    }

    /**
     * 设置生成规则
     *
     * @param termRule 生成规则
     */
    public void setTermRule(String termRule) {
        this.termRule = termRule;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取操作人ID
     *
     * @return create_user_id - 操作人ID
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 设置操作人ID
     *
     * @param createUserId 操作人ID
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取数据状态
     *
     * @return data_status - 数据状态
     */
    public Byte getDataStatus() {
        return dataStatus;
    }

    /**
     * 设置数据状态
     *
     * @param dataStatus 数据状态
     */
    public void setDataStatus(Byte dataStatus) {
        this.dataStatus = dataStatus;
    }

	public Integer getCodeId() {
		return codeId;
	}

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getVersionControl() {
        return versionControl;
    }

    public void setVersionControl(String versionControl) {
        this.versionControl = versionControl;
    }

    public String getDataModule() {
        return dataModule;
    }

    public void setDataModule(String dataModule) {
        this.dataModule = dataModule;
    }

    @Override
    public RdeModelAntiFraudRuleRecord clone() throws CloneNotSupportedException {
        return (RdeModelAntiFraudRuleRecord) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdeModelAntiFraudRuleRecord that = (RdeModelAntiFraudRuleRecord) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(codeId, that.codeId) &&
                Objects.equals(modelId, that.modelId) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(descr, that.descr) &&
                Objects.equals(control, that.control) &&
                Objects.equals(status, that.status) &&
                Objects.equals(term, that.term) &&
                Objects.equals(termPackage, that.termPackage) &&
                Objects.equals(termKey, that.termKey) &&
                Objects.equals(termValue, that.termValue) &&
                Objects.equals(termOther, that.termOther) &&
                Objects.equals(termRule, that.termRule) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(createUserId, that.createUserId) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(dataStatus, that.dataStatus) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(deptFlag, that.deptFlag) &&
                Objects.equals(ruleCode, that.ruleCode) &&
                Objects.equals(projectCode, that.projectCode) &&
                Objects.equals(businessCode, that.businessCode) &&
                Objects.equals(versionControl, that.versionControl) &&
                Objects.equals(dataModule, that.dataModule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupId, codeId, modelId, code, name, descr, control, status, term, termPackage, termKey, termValue, termOther, termRule, remark, createUserId, createTime, updateTime, dataStatus, deptId, deptFlag, ruleCode, projectCode, businessCode, versionControl, dataModule);
    }

    @Override
    public String toString() {
        return "RdeModelAntiFraudRuleRecord{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", codeId=" + codeId +
                ", modelId=" + modelId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", control='" + control + '\'' +
                ", status='" + status + '\'' +
                ", term='" + term + '\'' +
                ", termPackage='" + termPackage + '\'' +
                ", termKey='" + termKey + '\'' +
                ", termValue='" + termValue + '\'' +
                ", termOther='" + termOther + '\'' +
                ", termRule='" + termRule + '\'' +
                ", remark='" + remark + '\'' +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", dataStatus=" + dataStatus +
                ", deptId=" + deptId +
                ", deptFlag='" + deptFlag + '\'' +
                ", ruleCode='" + ruleCode + '\'' +
                ", projectCode='" + projectCode + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", versionControl='" + versionControl + '\'' +
                ", dataModule='" + dataModule + '\'' +
                '}';
    }
}