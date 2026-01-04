package com.value.decision.model.rdenew.vo;

import com.value.decision.model.rdenew.domain.*;

import java.util.List;

public class RdeModelDecisionCodeLevelVO extends RdeModelDecisionCodeLevel {

    private List<RdeModelRuleActivityRecord> termArray;

    private List<RdeModelRuleProperty> propertyArray;

    private List<RdeModelRuleMethod> methodArray;
    
    private List<RdeRiskVariableTheme> objectArray;
    
    private List<String> themeArray;

    private String themeIds;

    private String moduleId;

	public List<RdeModelRuleActivityRecord> getTermArray() {
        return termArray;
    }

    public void setTermArray(List<RdeModelRuleActivityRecord> termArray) {
        this.termArray = termArray;
    }

    public List<RdeModelRuleProperty> getPropertyArray() {
        return propertyArray;
    }

    public void setPropertyArray(List<RdeModelRuleProperty> propertyArray) {
        this.propertyArray = propertyArray;
    }

    public List<RdeModelRuleMethod> getMethodArray() {
        return methodArray;
    }

    public void setMethodArray(List<RdeModelRuleMethod> methodArray) {
        this.methodArray = methodArray;
    }

	public List<String> getThemeArray() {
		return themeArray;
	}

	public void setThemeArray(List<String> themeArray) {
		this.themeArray = themeArray;
	}

	public String getThemeIds() {
		return themeIds;
	}

	public void setThemeIds(String themeIds) {
		this.themeIds = themeIds;
	}

	public List<RdeRiskVariableTheme> getObjectArray() {
		return objectArray;
	}

	public void setObjectArray(List<RdeRiskVariableTheme> objectArray) {
		this.objectArray = objectArray;
	}

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
