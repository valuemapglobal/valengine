package com.value.decision.version.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反欺诈模型表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelAntiFraudVersionMapper extends CommonMapper<RdeModelAntiFraudVersion> {

    //批量插入
    public void insertBatch(@Param("fraudList") List<RdeModelAntiFraudVersion> fraudList);
}
