package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
@Table(name = "rde_model_decision_rule_record")
public class RdeModelDecisionRuleRecord extends BaseVO {
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
     * 组ID
     */
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 标识
     */
    private String code;

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
    private Byte dataStatus;

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
     * 获取组ID
     *
     * @return code_id - 组ID
     */
    public Integer getCodeId() {
        return codeId;
    }

    /**
     * 设置组ID
     *
     * @param codeId 组ID
     */
    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
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
}