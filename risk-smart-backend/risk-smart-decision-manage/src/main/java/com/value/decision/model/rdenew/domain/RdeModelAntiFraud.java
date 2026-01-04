package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.risksmart.common.core.web.domain.BaseVO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "rde_model_anti_fraud")
public class RdeModelAntiFraud extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(min = 1, max = 50, message = "名称长度在1-50字符之间")
    private String name;

    /**
     * 描述
     */
    @Length(min = 0, max = 200, message = "描述长度在0-200字符之间")
    private String descr;

    /**
     * 状态
     */
    private String status;
    
    /**
     * 新增类型
     */
    @Column(name = "model_type")
    private String modelType;
    /**
     * 验证状态（0未验证，1已验证，2验证失败）
     */
    @Column(name = "check_status")
    private Byte checkStatus;

    /**
     * 规则类型
     */
    @Column(name = "rule_type")
    private Integer ruleType;
    
    /**
     * 部门id
     */
    @Column(name = "dept_id")
    private Integer deptId;

    /**
     * 部门标识
     */
    @Column(name = "dept_flag")
    private String deptFlag;
    
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
     * 页面标识 分类为1,规则为0
     */
    @NotBlank(message = "策略模型标识不能为空")
    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
//    @NotBlank(message = "左侧产品导航标识不能为空")
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
    private List<RdeModelAntiFraudRuleGroup> groups;

    public List<RdeModelAntiFraudRuleGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<RdeModelAntiFraudRuleGroup> groups) {
        this.groups = groups;
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
     * 获取验证状态（0未验证，1已验证，2验证失败）
     *
     * @return check_status - 验证状态（0未验证，1已验证，2验证失败）
     */
    public Byte getCheckStatus() {
        return checkStatus;
    }

    /**
     * 设置验证状态（0未验证，1已验证，2验证失败）
     *
     * @param checkStatus 验证状态（0未验证，1已验证，2验证失败）
     */
    public void setCheckStatus(Byte checkStatus) {
        this.checkStatus = checkStatus;
    }

    /**
     * 获取规则类型
     *
     * @return rule_type - 规则类型
     */
    public Integer getRuleType() {
        return ruleType;
    }

    /**
     * 设置规则类型
     *
     * @param ruleType 规则类型
     */
    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
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

    /**
     * 获取新规则
     *
     * @return new_rule - 新规则
     */
    public String getNewRule() {
        return newRule;
    }

    /**
     * 设置新规则
     *
     * @param newRule 新规则
     */
    public void setNewRule(String newRule) {
        this.newRule = newRule;
    }

    /**
     * 获取运行规则
     *
     * @return run_rule - 运行规则
     */
    public String getRunRule() {
        return runRule;
    }

    /**
     * 设置运行规则
     *
     * @param runRule 运行规则
     */
    public void setRunRule(String runRule) {
        this.runRule = runRule;
    }

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}

    public String getDeptFlag() {
        return deptFlag;
    }

    public void setDeptFlag(String deptFlag) {
        this.deptFlag = deptFlag;
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
}