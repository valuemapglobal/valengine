package com.value.decision.model.rdenew.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.value.decision.model.decisionmanage.model.vo.GenerateRuleVO;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.domain.RdeModelRuleProperty;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RdeModelDecisionCodeLevelVO2 extends RdeModelDecisionCodeLevel {

    //条件集合
    private List<RdeRiskVariableConditionVO> conditionArray;
    //更新对象集合
    private List<RdeRiskVariableConditionVO> modifyArray;
    //属性集合
    private List<RdeModelRuleProperty> propertyArray;
    
    private Integer modelId;
    
    private Integer groupId;

	private Integer recordId;

    @NotBlank(message = "左侧产品导航标识不能为空")
    private String projectCode;

    @NotBlank(message = "策略模型标识不能为空")
    private String ruleCode;

    @TableField(exist = false)
    private GenerateRuleVO generateRuleVO;

    //数据标识  数据类型 1元数据 2特征变量 3分析指标
    @TableField(exist = false)
    private String dataType;

	private Integer salience;

    
    
    
    
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public Integer getModelId() {
		return modelId;
	}
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public List<RdeRiskVariableConditionVO> getConditionArray() {
		return conditionArray;
	}
	public void setConditionArray(List<RdeRiskVariableConditionVO> conditionArray) {
		this.conditionArray = conditionArray;
	}
	public List<RdeRiskVariableConditionVO> getModifyArray() {
		return modifyArray;
	}
	public void setModifyArray(List<RdeRiskVariableConditionVO> modifyArray) {
		this.modifyArray = modifyArray;
	}
	public List<RdeModelRuleProperty> getPropertyArray() {
		return propertyArray;
	}
	public void setPropertyArray(List<RdeModelRuleProperty> propertyArray) {
		this.propertyArray = propertyArray;
	}

	@Override
	public String getRuleCode() {
		return ruleCode;
	}

	@Override
	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}
}
