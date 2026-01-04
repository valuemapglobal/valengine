package com.value.decision.model.rdenew.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiProjectFraud;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 决策引擎项目-模型关联持久层接口
 * @author Raysen
 * @create 2023/4/14 9:16
 */
public interface RdeModelAntiProjectFraudMapper extends CommonMapper<RdeModelAntiProjectFraud> {


    @Select("select name,descr from rde_model_anti_fraud where id=#{modelId}")
    RdeModelAntiFraud selectById(@Param("modelId") Integer modelId);

    //根据model_id分组，查询指定项目模块code的模型集合
    @Select("select rule_id from rde_model_anti_project_fraud where data_status=0 and rule_id is not null and project_code=#{projectCode} group by rule_id")
    List<RdeModelAntiProjectFraud> getProjectModelList(RdeModelAntiProjectFraud record);

//    @Select("select * from rde_model_anti_project_fraud where rde_model_anti_project_fraud.model_id = #{modelId} and project_code=#{projectCode}")
//    RdeModelAntiProjectFraud selectModelIdAndProjectCode(@Param("modelId") Integer modelId,@Param("projectCode") String projectCode);

    @Select("select project_code,model_id,group_id,rule_id from rde_model_anti_project_fraud where rde_model_anti_project_fraud.model_id = #{modelId} and project_code=#{projectCode}")
    List<RdeModelAntiProjectFraud> selectByModelIdAndProjectCode(@Param("modelId") Integer modelId,@Param("projectCode") String projectCode);

    @Select("select project_code,model_id,group_id,rule_id from rde_model_anti_project_fraud where rde_model_anti_project_fraud.group_id = #{groupId} and project_code=#{projectCode}")
    List<RdeModelAntiProjectFraud> selectByGroupId(@Param("groupId") Integer groupId,@Param("projectCode") String projectCode);

    @Select("select project_code,model_id,group_id,rule_id from rde_model_anti_project_fraud where rde_model_anti_project_fraud.rule_id = #{ruleId} and project_code=#{projectCode}")
    List<RdeModelAntiProjectFraud> selectByRuleId(@Param("ruleId") Integer ruleId,@Param("projectCode") String projectCode);

//    @Delete("delete from rde_model_anti_project_fraud where project_code=#{projectCode} and model_id =#{modelId} and group_id =#{groupId} and rule_id =#{ruleId}")
    int delete(RdeModelAntiProjectFraud record);



}