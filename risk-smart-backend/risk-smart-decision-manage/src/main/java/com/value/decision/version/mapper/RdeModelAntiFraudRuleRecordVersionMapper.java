package com.value.decision.version.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.version.domain.RdeModelAntiFraudRuleRecordVersion;
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
public interface RdeModelAntiFraudRuleRecordVersionMapper extends CommonMapper<RdeModelAntiFraudRuleRecordVersion> {


    //根据规则组id查询 所有规则
    @Select("  select l.*\n" +
            "        from rde_model_anti_fraud_rule_record_version r\n" +
            "                 inner JOIN  rde_model_decision_code_level_version  l on r.code_id = l.id and r.version_control = l.version_control \n" +
            "        WHERE  r.data_status=0 and l.data_status = 0 and r.group_id = #{groupId} and r.status=1 and l.status = 1 and l.version_control = #{versionControl}")
    List<RdeModelDecisionCodeLevel> queryDataModule(@Param("groupId")Integer groupId,@Param("versionControl")String versionControl);

    //根据code 模型id 获取规则详情
    @Select("SELECT le.code code,le.level riskLevel,le.content riskStatement,le.strongly_reject stronglyReject,g.name groupName,le.version_control versionControl FROM `rde_model_decision_code_level_version` le inner join rde_model_anti_fraud_rule_record_version  re on le.id = re.code_id and le.version_control = re.version_control inner join rde_model_anti_fraud_rule_group_version g on re.group_id = g.id and re.version_control = g.version_control  \n" +
            "where re.data_status=0 and le.data_status = 0 and re.status=1 and le.status = 1 AND re.model_id = #{modelId} and le.code = #{code} and le.version_control = #{versionControl} ")
    Map<String,Object> getDecisionLevelDeatil(@Param("code")String code, @Param("modelId")String modelId,@Param("versionControl")String versionControl);

    //批量插入
    public void insertBatch(@Param("fraudRecordList") List<RdeModelAntiFraudRuleRecordVersion> fraudRecordList);
}
