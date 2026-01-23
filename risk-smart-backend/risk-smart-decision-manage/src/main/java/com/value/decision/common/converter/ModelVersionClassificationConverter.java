package com.value.decision.common.converter;


import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.version.domain.ModelVersionClassification;

import java.util.Date;

/**
 * 模型版本分类转换器
 *
 * @author vlauemap team
 * @since 2026/01/22
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
