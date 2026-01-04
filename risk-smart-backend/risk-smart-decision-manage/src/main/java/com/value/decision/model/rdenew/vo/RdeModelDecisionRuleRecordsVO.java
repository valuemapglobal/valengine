package com.value.decision.model.rdenew.vo;

import java.util.List;

public class RdeModelDecisionRuleRecordsVO {

    Integer groupId;
    List<Integer> decisionCodeList;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Integer> getDecisionCodeList() {
        return decisionCodeList;
    }

    public void setDecisionCodeList(List<Integer> decisionCodeList) {
        this.decisionCodeList = decisionCodeList;
    }
}
