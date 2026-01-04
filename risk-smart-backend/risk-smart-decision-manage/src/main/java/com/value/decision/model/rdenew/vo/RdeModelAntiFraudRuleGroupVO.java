package com.value.decision.model.rdenew.vo;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;

/**
 * @ClassName RdeModelAntiFraudRuleGroupVO
 * @Description TODO
 * @Authot Administrator
 * @Date 2023/4/20 18:15
 **/
public class RdeModelAntiFraudRuleGroupVO extends RdeModelAntiFraudRuleGroup {
    /*
     *  状态字段 0为关  1为开
     */
    private String flag;
    
    private String content;
    
   /* private String projectCode;
    
    

    public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}*/

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
