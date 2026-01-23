package com.value.data.domain.vo;

import com.value.data.domain.entity.RdeRiskVariableGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RdeRiskVariableGroupVO extends RdeRiskVariableGroup {
    private String associatedInterfaces;
}
