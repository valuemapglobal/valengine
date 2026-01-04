package com.value.decision.snapshot.mapper;

import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelAntiProjectFraud;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 反欺诈模型规则明细表 Mapper 接口
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
public interface RdeModelAntiFraudRuleRecordSnapshotMapper extends CommonMapper<RdeModelAntiFraudRuleRecordSnapshot> {

    //清除快照表已存在的数据
    public void deleteList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode);

    //清除快照表已存在的数据
    public void deleteList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode,@Param("businessCode") String businessCode);

    //根据模型id查询ruleId
    public List<Integer> queryList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode);

    //根据模型id查询ruleId
    public List<Integer> queryList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode,@Param("businessCode") String businessCode);

    //根据模型id查询codeId
    public List<Integer> codeIdQueryList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode);

    //根据模型id查询codeId
    public List<Integer> codeIdQueryList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode,@Param("businessCode") String businessCode);

    public List<RdeModelDecisionCodeLevelSnapshot> selectModelRecordList(@Param("modelList") List<Integer> modelIdList, @Param("projectCode") String projectCode);

    //查询部门下决策集合
    @Select("select id as rule_id from rde_model_anti_fraud_rule_record_snapshot where data_status=0 and rule_code=6 and dept_id=#{deptId} and project_code=#{projectCode} group by rule_id")
    List<RdeModelAntiProjectFraud> getProjectModelList(RdeModelAntiProjectFraud record);

    List<RdeModelDecisionCodeLevel> newList2(@Param("groupId")Integer groupId);

    //根据code 模型id 获取规则详情
    @Select("SELECT le.code code,le.level riskLevel,le.content riskStatement,le.strongly_reject stronglyReject,g.name groupName,le.quato_rate quatoRate FROM `rde_model_decision_code_level_snapshot` le inner join rde_model_anti_fraud_rule_record_snapshot  re on le.id = re.code_id  inner join rde_model_anti_fraud_rule_group_snapshot g on re.group_id = g.id   \n" +
            "where re.model_id = #{modelId} and le.code = #{code} ")
    Map<String,Object> getDecisionLevelDeatil(@Param("code")String code, @Param("modelId")String modelId);
}
