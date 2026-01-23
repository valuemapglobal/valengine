package com.value.decision.snapshot.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.snapshot.domain.RdeModelTestResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 模型测试结果表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelTestResultMapper extends CommonMapper<RdeModelTestResult> {
    void changeHitStatus(@Param("list") List<Integer> ruleIdList, String serialNumber, String ruleCode, String projectCode);

    @Select("select * from rde_model_test_result where serial_number=#{serialNumber} and rule_code=#{ruleCode} and project_code=#{projectCode}")
    List<RdeModelTestResult> getAllBySerialNumber(@Param("serialNumber") String serialNumber, @Param("ruleCode") String ruleCode, @Param("projectCode") String projectCode);
}
