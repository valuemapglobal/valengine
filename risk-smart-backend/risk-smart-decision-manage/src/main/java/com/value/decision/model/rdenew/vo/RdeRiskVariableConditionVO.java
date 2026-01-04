package com.value.decision.model.rdenew.vo;

import java.util.List;

/**
 * 决策引擎页面配置
 */
public class RdeRiskVariableConditionVO {

    private Integer id;
    // 条件
    private String operator;
    // 值结果
    private String result;
    // 选择对象
    private List<String> selectObj;
    // 或条件
    private List<RdeRiskVariableConditionVO> condition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<String> getSelectObj() {
        return selectObj;
    }

    public void setSelectObj(List<String> selectObj) {
        this.selectObj = selectObj;
    }

    public List<RdeRiskVariableConditionVO> getCondition() {
        return condition;
    }

    public void setCondition(List<RdeRiskVariableConditionVO> condition) {
        this.condition = condition;
    }
}
