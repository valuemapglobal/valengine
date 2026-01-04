package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
@Table(name = "rde_model_decision")
public class RdeModelDecision extends BaseVO {
    @Id
    @GeneratedValue(generator = "JDBC")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String descr;

    /**
     * 状态
     */
    private String status;

    /**
     * 验证状态（0未验证，1已验证，2验证失败）
     */
    @Column(name = "check_status")
    private Byte checkStatus;

    /**
     * 规则类型（
1反欺诈模型 
2申请模型
3准入模型 
4黑灰名单模型 
5评分模型 
6财务测算模型
7额度测算模型 
8风险定价模型 
9贷后监控模型 
10违约预测模型 
11宏观预测模型 
12行业预测模型 
13地域风险模型）
     */
    @Column(name = "rule_type")
    private Integer ruleType;

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
     * 获取规则类型（
1反欺诈模型 
2申请模型
3准入模型 
4黑灰名单模型 
5评分模型 
6财务测算模型
7额度测算模型 
8风险定价模型 
9贷后监控模型 
10违约预测模型 
11宏观预测模型 
12行业预测模型 
13地域风险模型）
     *
     * @return rule_type - 规则类型（
1反欺诈模型 
2申请模型
3准入模型 
4黑灰名单模型 
5评分模型 
6财务测算模型
7额度测算模型 
8风险定价模型 
9贷后监控模型 
10违约预测模型 
11宏观预测模型 
12行业预测模型 
13地域风险模型）
     */
    public Integer getRuleType() {
        return ruleType;
    }

    /**
     * 设置规则类型（
1反欺诈模型 
2申请模型
3准入模型 
4黑灰名单模型 
5评分模型 
6财务测算模型
7额度测算模型 
8风险定价模型 
9贷后监控模型 
10违约预测模型 
11宏观预测模型 
12行业预测模型 
13地域风险模型）
     *
     * @param ruleType 规则类型（
1反欺诈模型 
2申请模型
3准入模型 
4黑灰名单模型 
5评分模型 
6财务测算模型
7额度测算模型 
8风险定价模型 
9贷后监控模型 
10违约预测模型 
11宏观预测模型 
12行业预测模型 
13地域风险模型）
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
}