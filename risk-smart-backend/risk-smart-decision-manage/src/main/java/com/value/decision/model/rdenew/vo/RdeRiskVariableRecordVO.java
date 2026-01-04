package com.value.decision.model.rdenew.vo;

import com.value.decision.model.rdenew.domain.RdeRiskVariableRecord;

import jakarta.persistence.Transient;

public class RdeRiskVariableRecordVO extends RdeRiskVariableRecord {
    @Transient
    private Integer objectId;

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
}
