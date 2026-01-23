package com.value.decision.model.rdenew.domain;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;
import com.value.decision.model.decisionmanage.model.vo.WarnComparisonVO;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "rde_model_decision_code_level")
public class RdeModelDecisionCodeLevel extends BaseVO {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * code
     */
    private String code;

    /**
     * 等级
     */
    private String level;
    /**
     * 0评分策略，1风险预警策略。2准入策略
     */
    private Integer type;

    
    /**
     * 说明
     */
    @Column(name = "content")
    private String content;

    /**
     * 话术
     */
    @Column(name = "risk_description")
    private String riskDescription;

    /**
     * 排序
     */
    @Column(name = "sort_num")
    private Integer sortNum;

    /**
     * 开始比例%
     */
    @Column(name = "start_rate")
    private BigDecimal startRate;

    /**
     * 结束比例%
     */
    @Column(name = "end_rate")
    private BigDecimal endRate;

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
     * 更新人
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 更新名称
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Byte dataStatus;
    
    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "dept_flag")
    private String deptFlag;

    /**
     * 项目代码;银行流水1001,支付流水1002
     */
    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "rule_code")
    private String ruleCode;

    /**
     * 分类名称
     */
    @Column(name = "purpose_category")
    private String purposeCategory;

    /**
     * 流水性质
     * 1->经营性收入
     * 2->经营性支出
     * 3->经营性支出
     * 4->非经营性支出
     */
    @Column(name = "category_type")
    private String categoryType;

    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景标识不能为空")
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 版本控制
     */
    @Column(name = "version_control")
    private String versionControl;

    @Column(name = "data_module")
    private String dataModule;

    //判断条件
    @Column(name = "conditions")
    private String conditions;

    @TableField(exist = false)
    private JSONArray conditionsJSON;

    @TableField(exist = false)
    private List<WarnComparisonVO> termWarn;

    @Column(name = "strongly_reject")
    private String stronglyReject;

    @Column(name = "quato_rate")
    private String quatoRate;

    @Column(name = "transfer_to_person")
    private String transferToPerson;

    private Integer riskType;

    public String getTransferToPerson() {
        return transferToPerson;
    }

    public void setTransferToPerson(String transferToPerson) {
        this.transferToPerson = transferToPerson;
    }

    public Integer getRiskType() {
        return riskType;
    }

    public void setRiskType(Integer riskType) {
        this.riskType = riskType;
    }

    public String getPurposeCategory() {
        return purposeCategory;
    }

    public void setPurposeCategory(String purposeCategory) {
        this.purposeCategory = purposeCategory;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
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
     * 获取id
     *
     * @return id - id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取code
     *
     * @return code - code
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置code
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取等级
     *
     * @return level - 等级
     */
    public String getLevel() {
        return level;
    }

    /**
     * 设置等级
     *
     * @param level 等级
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 获取说明
     *
     * @return content - 说明
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置说明
     *
     * @param content 说明
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取话术
     *
     * @return risk_description - 话术
     */
    public String getRiskDescription() {
        return riskDescription;
    }

    /**
     * 设置话术
     *
     * @param riskDescription 话术
     */
    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }

    /**
     * 获取排序
     *
     * @return sort_num - 排序
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * 设置排序
     *
     * @param sortNum 排序
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    /**
     * 获取开始比例%
     *
     * @return start_rate - 开始比例%
     */
    public BigDecimal getStartRate() {
        return startRate;
    }

    /**
     * 设置开始比例%
     *
     * @param startRate 开始比例%
     */
    public void setStartRate(BigDecimal startRate) {
        this.startRate = startRate;
    }

    /**
     * 获取结束比例%
     *
     * @return end_rate - 结束比例%
     */
    public BigDecimal getEndRate() {
        return endRate;
    }

    /**
     * 设置结束比例%
     *
     * @param endRate 结束比例%
     */
    public void setEndRate(BigDecimal endRate) {
        this.endRate = endRate;
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
     * 获取更新人
     *
     * @return update_by - 更新人
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 设置更新人
     *
     * @param updateBy 更新人
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 获取更新名称
     *
     * @return create_by - 更新名称
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置更新名称
     *
     * @param createBy 更新名称
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public JSONArray getConditionsJSON() {
        return conditionsJSON;
    }

    public void setConditionsJSON(JSONArray conditionsJSON) {
        this.conditionsJSON = conditionsJSON;
    }

    public String getStronglyReject() {
        return stronglyReject;
    }

    public void setStronglyReject(String stronglyReject) {
        this.stronglyReject = stronglyReject;
    }

    public String getQuatoRate() {
        return quatoRate;
    }

    public void setQuatoRate(String quatoRate) {
        this.quatoRate = quatoRate;
    }
}