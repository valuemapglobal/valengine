package com.value.decision.version.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 反欺诈模型表 Mapper 接口
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
public interface RdeModelAntiFraudVersionMapper extends CommonMapper<RdeModelAntiFraudVersion> {

    //批量插入
    public void insertBatch(@Param("fraudList") List<RdeModelAntiFraudVersion> fraudList);
}
