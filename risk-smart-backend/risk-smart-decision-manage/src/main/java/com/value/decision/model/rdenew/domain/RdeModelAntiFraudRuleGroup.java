package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.risksmart.common.core.web.domain.BaseVO;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Table(name = "rde_model_anti_fraud_rule_group")
public class RdeModelAntiFraudRuleGroup extends BaseVO implements Cloneable {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 模型ID
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length( max = 50, message = "名称不可以超过50个字符")
    private String name;

    /**
     * 描述
     */
    @Length( max = 200, message = "名称不可以超过200个字符")
    private String descr;

    /**
     * 状态
     */
    private String status;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "update_time")
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
    @NotBlank(message = "策略模型标识不能为空")
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @NotBlank(message = "左侧产品导航标识不能为空")
    @Column(name = "project_code")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景导航标识不能为空")
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;

    @TableField(exist = false)
    private List<RdeModelAntiFraudRuleRecord> rules;

    public List<RdeModelAntiFraudRuleRecord> getRules() {
        return rules;
    }

    public void setRules(List<RdeModelAntiFraudRuleRecord> rules) {
        this.rules = rules;
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
     * 获取模型ID
     *
     * @return model_id - 模型ID
     */
    public Integer getModelId() {
        return modelId;
    }

    /**
     * 设置模型ID
     *
     * @param modelId 模型ID
     */
    public void setModelId(Integer modelId) {
        this.modelId = modelId;
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

    @Override
    public RdeModelAntiFraudRuleGroup clone() throws CloneNotSupportedException {
        return  (RdeModelAntiFraudRuleGroup) super.clone();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RdeModelAntiFraudRuleGroup that = (RdeModelAntiFraudRuleGroup) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(modelId, that.modelId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(descr, that.descr) &&
                Objects.equals(status, that.status) &&
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
                Objects.equals(versionControl, that.versionControl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modelId, name, descr, status, remark, createUserId, createTime, updateTime, dataStatus, deptId, deptFlag, ruleCode, projectCode, businessCode, versionControl);
    }

    @Override
    public String toString() {
        return "RdeModelAntiFraudRuleGroup{" +
                "id=" + id +
                ", modelId=" + modelId +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", status='" + status + '\'' +
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
                '}';
    }
}