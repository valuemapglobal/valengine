package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
@Table(name = "rde_model_rule_method")
public class RdeModelRuleMethod extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
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
     * 方法名称
     */
    private String name;

    /**
     * 对象别名
     */
    @Column(name = "obj_alias")
    private String objAlias;

    /**
     * 对象属性
     */
    @Column(name = "key_code")
    private String keyCode;

    /**
     * 对象属性值
     */
    @Column(name = "key_value")
    private String keyValue;

    /**
     * 描述
     */
    private String descr;

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
     * 获取方法名称
     *
     * @return name - 方法名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置方法名称
     *
     * @param name 方法名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取对象别名
     *
     * @return obj_alias - 对象别名
     */
    public String getObjAlias() {
        return objAlias;
    }

    /**
     * 设置对象别名
     *
     * @param objAlias 对象别名
     */
    public void setObjAlias(String objAlias) {
        this.objAlias = objAlias;
    }

    /**
     * 获取对象属性
     *
     * @return key_code - 对象属性
     */
    public String getKeyCode() {
        return keyCode;
    }

    /**
     * 设置对象属性
     *
     * @param keyCode 对象属性
     */
    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * 获取对象属性值
     *
     * @return key_value - 对象属性值
     */
    public String getKeyValue() {
        return keyValue;
    }

    /**
     * 设置对象属性值
     *
     * @param keyValue 对象属性值
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