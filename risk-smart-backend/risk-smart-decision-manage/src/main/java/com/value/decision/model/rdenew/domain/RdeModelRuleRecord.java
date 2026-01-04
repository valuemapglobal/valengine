package com.value.decision.model.rdenew.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.risksmart.common.core.web.domain.BaseVO;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Table(name = "rde_model_rule_record")
public class RdeModelRuleRecord extends BaseVO {
    /**
     * 自增长ID
     */
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
     * 分析对象ids，第一级是大对象id，
     */
    @Column(name = "theme_ids")
    private String themeIds;
    /**
     * 分析对象id
     */
    @Column(name = "object_id")
    private Integer objectId;

    /**
     * 规则顺序
     */
    private Integer sorder;

    /**
     * 类型:1wehn条件 2.then响应code,3更新对象
     */
    @Column(name = "rule_type")
    private Byte ruleType;

    /**
     * 规则对象别名(限200个英文字符)
     */
    @Column(name = "rule_obj_alias")
    private String ruleObjAlias;

    /**
     * 规则对象(限200个英文字符)
     */
    @Column(name = "rule_obj")
    private String ruleObj;

    /**
     * 规则属性(限200个英文字符)
     */
    @Column(name = "rule_properties")
    private String ruleProperties;

    /**
     * 规则运算符(限200个英文字符)
     */
    @Column(name = "rule_operator")
    private String ruleOperator;

    /**
     * 规则值(限200个英文字符)
     */
    @Column(name = "rule_value")
    private String ruleValue;

    /**
     * 拼接之后的包路径
     */
    private String packages;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改者
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 数据状态
     */
    @Column(name = "data_status")
    private Byte dataStatus;
    
    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "rule_code")
    private String ruleCode;
    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景标识不能为空")
    @Column(name = "business_code")
    private String businessCode;

    /**
     * 获取自增长ID
     *
     * @return id - 自增长ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置自增长ID
     *
     * @param id 自增长ID
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
     * 获取分析对象ids，第一级是大对象id，
     *
     * @return theme_ids - 分析对象ids，第一级是大对象id，
     */
    public String getThemeIds() {
        return themeIds;
    }

    /**
     * 设置分析对象ids，第一级是大对象id，
     *
     * @param themeIds 分析对象ids，第一级是大对象id，
     */
    public void setThemeIds(String themeIds) {
        this.themeIds = themeIds;
    }

    /**
     * 获取规则顺序
     *
     * @return sorder - 规则顺序
     */
    public Integer getSorder() {
        return sorder;
    }

    /**
     * 设置规则顺序
     *
     * @param sorder 规则顺序
     */
    public void setSorder(Integer sorder) {
        this.sorder = sorder;
    }

    /**
     * 获取类型:1wehn条件 2.then响应code,3更新对象
     *
     * @return rule_type - 类型:1wehn条件 2.then响应code,3更新对象
     */
    public Byte getRuleType() {
        return ruleType;
    }

    /**
     * 设置类型:1wehn条件 2.then响应code,3更新对象
     *
     * @param ruleType 类型:1wehn条件 2.then响应code,3更新对象
     */
    public void setRuleType(Byte ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * 获取规则对象别名(限200个英文字符)
     *
     * @return rule_obj_alias - 规则对象别名(限200个英文字符)
     */
    public String getRuleObjAlias() {
        return ruleObjAlias;
    }

    /**
     * 设置规则对象别名(限200个英文字符)
     *
     * @param ruleObjAlias 规则对象别名(限200个英文字符)
     */
    public void setRuleObjAlias(String ruleObjAlias) {
        this.ruleObjAlias = ruleObjAlias;
    }

    /**
     * 获取规则对象(限200个英文字符)
     *
     * @return rule_obj - 规则对象(限200个英文字符)
     */
    public String getRuleObj() {
        return ruleObj;
    }

    /**
     * 设置规则对象(限200个英文字符)
     *
     * @param ruleObj 规则对象(限200个英文字符)
     */
    public void setRuleObj(String ruleObj) {
        this.ruleObj = ruleObj;
    }

    /**
     * 获取规则属性(限200个英文字符)
     *
     * @return rule_properties - 规则属性(限200个英文字符)
     */
    public String getRuleProperties() {
        return ruleProperties;
    }

    /**
     * 设置规则属性(限200个英文字符)
     *
     * @param ruleProperties 规则属性(限200个英文字符)
     */
    public void setRuleProperties(String ruleProperties) {
        this.ruleProperties = ruleProperties;
    }

    /**
     * 获取规则运算符(限200个英文字符)
     *
     * @return rule_operator - 规则运算符(限200个英文字符)
     */
    public String getRuleOperator() {
        return ruleOperator;
    }

    /**
     * 设置规则运算符(限200个英文字符)
     *
     * @param ruleOperator 规则运算符(限200个英文字符)
     */
    public void setRuleOperator(String ruleOperator) {
        this.ruleOperator = ruleOperator;
    }

    /**
     * 获取规则值(限200个英文字符)
     *
     * @return rule_value - 规则值(限200个英文字符)
     */
    public String getRuleValue() {
        return ruleValue;
    }

    /**
     * 设置规则值(限200个英文字符)
     *
     * @param ruleValue 规则值(限200个英文字符)
     */
    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    /**
     * 获取拼接之后的包路径
     *
     * @return packages - 拼接之后的包路径
     */
    public String getPackages() {
        return packages;
    }

    /**
     * 设置拼接之后的包路径
     *
     * @param packages 拼接之后的包路径
     */
    public void setPackages(String packages) {
        this.packages = packages;
    }

    /**
     * 获取创建者
     *
     * @return create_by - 创建者
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置创建者
     *
     * @param createBy 创建者
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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
     * 获取修改者
     *
     * @return update_by - 修改者
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * 设置修改者
     *
     * @param updateBy 修改者
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
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

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public Integer getDeptId() {
		return deptId;
	}

	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
    
    
}