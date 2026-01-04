package com.value.decision.model.rdenew.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeRiskVariableRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RdeRiskVariableRecordMapper extends CommonMapper<RdeRiskVariableRecord> {
    List<RdeRiskVariableRecord> selectRecordByThemeIdAndKeycode(@Param("themeId") Integer themeId, @Param("keycode") String keycode);
}
