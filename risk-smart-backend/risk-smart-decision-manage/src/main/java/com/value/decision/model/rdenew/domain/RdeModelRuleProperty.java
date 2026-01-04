package com.value.decision.model.rdenew.domain;

import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import java.util.Date;

@Table(name = "rde_model_rule_property")
public class RdeModelRuleProperty extends BaseVO {
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 决策code id
     */
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 规则明细id
     */
    @Column(name = "rule_id")
    private Integer ruleId;

    /**
     * 名称
     */
    @Column(name = "key_code")
    private String keyCode;

    /**
     * 属性code
     */
    @Column(name = "key_value")
    private String keyValue;

    /**
     * 描述
     */
    private String descr;

    /**
     * 类型（预授信/授信）
     */
    private Integer type;

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
     * 获取决策code id
     *
     * @return code_id - 决策code id
     */
    public Integer getCodeId() {
        return codeId;
    }

    /**
     * 设置决策code id
     *
     * @param codeId 决策code id
     */
    public void setCodeId(Integer codeId) {
        this.codeId = codeId;
    }

    /**
     * 获取规则明细id
     *
     * @return rule_id - 规则明细id
     */
    public Integer getRuleId() {
        return ruleId;
    }

    /**
     * 设置规则明细id
     *
     * @param ruleId 规则明细id
     */
    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * 获取名称
     *
     * @return key_code - 名称
     */
    public String getKeyCode() {
        return keyCode;
    }

    /**
     * 设置名称
     *
     * @param keyCode 名称
     */
    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * 获取属性code
     *
     * @return key_value - 属性code
     */
    public String getKeyValue() {
        return keyValue;
    }

    /**
     * 设置属性code
     *
     * @param keyValue 属性code
     */
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
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
     * 获取类型（预授信/授信）
     *
     * @return type - 类型（预授信/授信）
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型（预授信/授信）
     *
     * @param type 类型（预授信/授信）
     */
    public void setType(Integer type) {
        this.type = type;
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