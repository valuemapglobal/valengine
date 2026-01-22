package com.risksmart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.risksmart.common.core.annotation.Excel;
import com.risksmart.common.core.annotation.Excel.ColumnType;
import com.risksmart.common.core.web.BaseEntity;

/**
 * 操作日志记录表 oper_log
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class SysOperLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    @Excel(name = "操作序号", cellType = ColumnType.NUMERIC)
    private Long operId;

    /** 操作模块 */
    @Excel(name = "操作模块")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @Excel(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据")
    private Integer businessType;

    /** 业务类型数组 */
    private Integer[] businessTypes;

    /** 请求方法 */
    @Excel(name = "请求方法")
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /** 操作人员 */
    @Excel(name = "操作人员")
    private String operName;

    /** 部门ID */
    private Integer deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 请求url */
    @Excel(name = "请求地址")
    private String operUrl;

    /** 操作地址 */
    @Excel(name = "操作地址")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数")
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数")
    private String jsonResult;

    /** 返回结果 */
    private String result;

    /** 操作状态（0正常 1异常） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private Integer status;

    /** 错误消息 */
    @Excel(name = "错误消息")
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    /** 消耗时间 */
    @Excel(name = "消耗时间", suffix = "毫秒")
    private Long costTime;

    // 决策引擎扩展字段
    /** 业务编码 */
    private String businessCode;
    /** 项目编码 */
    private String projectCode;
    /** 规则编码 */
    private String ruleCode;
    /** 操作点 */
    private String operationPoints;
    /** 策略模型名称 */
    private String policyModelName;
    /** 分值 */
    private String points;
    /** 主键 */
    private String primaryKey;
    /** 处理策略 */
    private String processStrategy;

    public Long getOperId() { return operId; }
    public void setOperId(Long operId) { this.operId = operId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getBusinessType() { return businessType; }
    public void setBusinessType(Integer businessType) { this.businessType = businessType; }

    public Integer[] getBusinessTypes() { return businessTypes; }
    public void setBusinessTypes(Integer[] businessTypes) { this.businessTypes = businessTypes; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

    public Integer getOperatorType() { return operatorType; }
    public void setOperatorType(Integer operatorType) { this.operatorType = operatorType; }

    public String getOperName() { return operName; }
    public void setOperName(String operName) { this.operName = operName; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getOperUrl() { return operUrl; }
    public void setOperUrl(String operUrl) { this.operUrl = operUrl; }

    public String getOperIp() { return operIp; }
    public void setOperIp(String operIp) { this.operIp = operIp; }

    public String getOperLocation() { return operLocation; }
    public void setOperLocation(String operLocation) { this.operLocation = operLocation; }

    public String getOperParam() { return operParam; }
    public void setOperParam(String operParam) { this.operParam = operParam; }

    public String getJsonResult() { return jsonResult; }
    public void setJsonResult(String jsonResult) { this.jsonResult = jsonResult; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public Date getOperTime() { return operTime; }
    public void setOperTime(Date operTime) { this.operTime = operTime; }

    public Long getCostTime() { return costTime; }
    public void setCostTime(Long costTime) { this.costTime = costTime; }

    // 决策引擎扩展字段 getter/setter
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String businessCode) { this.businessCode = businessCode; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }

    public String getOperationPoints() { return operationPoints; }
    public void setOperationPoints(String operationPoints) { this.operationPoints = operationPoints; }

    public String getPolicyModelName() { return policyModelName; }
    public void setPolicyModelName(String policyModelName) { this.policyModelName = policyModelName; }

    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }

    public String getPrimaryKey() { return primaryKey; }
    public void setPrimaryKey(String primaryKey) { this.primaryKey = primaryKey; }

    public String getProcessStrategy() { return processStrategy; }
    public void setProcessStrategy(String processStrategy) { this.processStrategy = processStrategy; }
}
