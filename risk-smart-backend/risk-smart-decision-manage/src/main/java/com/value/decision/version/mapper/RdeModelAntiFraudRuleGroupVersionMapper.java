package com.value.decision.version.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.version.domain.RdeModelAntiFraudRuleGroupVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 反欺诈模型规则组表 Mapper 接口
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
public interface RdeModelAntiFraudRuleGroupVersionMapper extends CommonMapper<RdeModelAntiFraudRuleGroupVersion> {

    //批量插入
    public void insertBatch(@Param("fraudGroupList") List<RdeModelAntiFraudRuleGroupVersion> fraudGroupList);
}
