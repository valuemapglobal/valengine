package com.value.decision.model.rdenew.vo;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraudObject;

import java.util.List;

public class RdeModelAntiFraudObjectVO extends RdeModelAntiFraudObject {
    private List<Integer> themeIdList;

    public List<Integer> getThemeIdList() {
        return themeIdList;
    }

    public void setThemeIdList(List<Integer> themeIdList) {
        this.themeIdList = themeIdList;
    }
}
