package com.value.decision.version.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.version.domain.RdeModelDecisionCodeLevelVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 决策code登记表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelDecisionCodeLevelVersionMapper extends CommonMapper<RdeModelDecisionCodeLevelVersion> {

    //批量插入
    public void insertBatch(@Param("CodeLevelList") List<RdeModelDecisionCodeLevelVersion> CodeLevelList);
}
