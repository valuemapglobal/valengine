package com.value.decision.model.rdenew.vo;

public class BankClassVO {

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 分类标识 0为规则 1为分类
     */
    private Integer ruleCode;

    /**
     * 模块标识 银行流水1001 支付流水1002
     */
    private Integer projectCode;

    /**
     * 企业ckey
     */
    private String ckey;

    /**
     * 企业名称
     */
    private String cname;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 状态 1为开启 0为关闭
     */
    private String status;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public Integer getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(Integer ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Integer getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(Integer projectCode) {
        this.projectCode = projectCode;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
