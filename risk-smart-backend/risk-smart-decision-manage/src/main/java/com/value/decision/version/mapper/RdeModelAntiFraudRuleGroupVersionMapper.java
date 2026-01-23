package com.value.decision.version.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.version.domain.RdeModelAntiFraudRuleGroupVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反欺诈模型规则组表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelAntiFraudRuleGroupVersionMapper extends CommonMapper<RdeModelAntiFraudRuleGroupVersion> {

    //批量插入
    public void insertBatch(@Param("fraudGroupList") List<RdeModelAntiFraudRuleGroupVersion> fraudGroupList);
}
