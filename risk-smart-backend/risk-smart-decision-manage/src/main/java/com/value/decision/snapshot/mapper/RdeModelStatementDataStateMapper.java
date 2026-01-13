package com.value.decision.snapshot.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.snapshot.domain.RdeModelStatementDataState;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Dianne
 * @since 2023-05-08
 */
public interface RdeModelStatementDataStateMapper extends CommonMapper<RdeModelStatementDataState> {

    @Select("select * from rde_model_statement_data_state where serial_number = #{serialNumber} and data_status=0 and rule_code=#{ruleCode} and project_code=#{projectCode}")
    RdeModelStatementDataState getOneBySerialNumber(@Param("serialNumber") String serialNumber, @Param("ruleCode") String ruleCode, @Param("projectCode") String projectCode);

    @Select("select * from rde_model_statement_data_state where serial_number = #{serialNumber} and data_status=0 and rule_code=#{ruleCode} and project_code=#{projectCode} and business_code = #{businessCode}")
    RdeModelStatementDataState getOneBySerialNumberAndBusinessCode(@Param("serialNumber") String serialNumber, @Param("ruleCode") String ruleCode, @Param("projectCode") String projectCode, @Param("businessCode") String businessCode);

    @Select("select * from rde_model_statement_data_state where user_id = #{userId} and data_status=0 and rule_code=#{ruleCode} and project_code=#{projectCode}")
    List<RdeModelStatementDataState> getAllByUserId(@Param("userId") Integer userId, @Param("ruleCode") String ruleCode, @Param("projectCode") String projectCode);
}
