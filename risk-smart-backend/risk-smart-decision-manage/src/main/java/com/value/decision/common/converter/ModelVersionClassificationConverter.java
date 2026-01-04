package com.value.decision.common.converter;


import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.version.domain.ModelVersionClassification;

import java.util.Date;

/**
 * @author Vida
 * @date 2025年04月02日 13:23
 * @description
 */
public class ModelVersionClassificationConverter {
    public static ModelVersionClassification convert(RdeModelAntiFraud rdeModelAntiFraud){
        final ModelVersionClassification mvc = new ModelVersionClassification();
        mvc.setModelId(rdeModelAntiFraud.getId());
        mvc.setModelName(rdeModelAntiFraud.getName());
        mvc.setVersionControl(rdeModelAntiFraud.getVersionControl());
        mvc.setCreateTime(new Date());
        mvc.setDeptId(rdeModelAntiFraud.getDeptId());
        mvc.setProjectCode(rdeModelAntiFraud.getProjectCode());
        mvc.setBusinessCode(rdeModelAntiFraud.getBusinessCode());
        mvc.setRuleCode(rdeModelAntiFraud.getRuleCode());
        return mvc;
    }
}
