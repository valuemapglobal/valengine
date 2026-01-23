package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
@Table(name = "rde_model_credit_score_grade")
public class RdeModelCreditScoreGrade extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 组ID
     */
    @Column(name = "model_id")
    private Integer modelId;

    /**
     * 名称
     */
    private String name;

    /**
     * 条件
     */
    private String term;

    /**
     * 条件值
     */
    @Column(name = "term_value")
    private String termValue;

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
     * @return model_id - 组ID
     */
    public Integer getModelId() {
        return modelId;
    }

    /**
     * 设置组ID
     *
     * @param modelId 组ID
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
     * 获取条件值
     *
     * @return term_value - 条件值
     */
    public String getTermValue() {
        return termValue;
    }

    /**
     * 设置条件值
     *
     * @param termValue 条件值
     */
    public void setTermValue(String termValue) {
        this.termValue = termValue;
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