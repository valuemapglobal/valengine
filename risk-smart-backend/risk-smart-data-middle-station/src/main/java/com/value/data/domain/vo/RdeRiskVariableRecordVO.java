package com.value.data.domain.vo;

import com.value.data.domain.entity.RdeRiskVariableRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RdeRiskVariableRecordVO extends RdeRiskVariableRecord {
    private String parentName;
}
