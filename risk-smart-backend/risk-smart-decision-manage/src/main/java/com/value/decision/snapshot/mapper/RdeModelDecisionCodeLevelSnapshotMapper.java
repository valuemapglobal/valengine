package com.value.decision.snapshot.mapper;

import com.value.decision.model.decisionmanage.model.vo.JudicialClassRuleVO;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.risksmart.common.core.base.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 决策code登记表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelDecisionCodeLevelSnapshotMapper extends CommonMapper<RdeModelDecisionCodeLevelSnapshot> {
    List<RdeModelDecisionCodeLevelSnapshot> selectSnapshotCodeList(@Param("codeList") List<String> codeList,@Param("projectCode") String projectCode);

    //清除快照表已存在的数据
    public void deleteList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode);

    //清除快照表已存在的数据
    public void deleteList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode")String ruleCode,@Param("businessCode") String businessCode);

    List<RdeModelDecisionCodeLevelSnapshot> selectListByStrategyId(@Param("moduleId") Integer moduleId);

    List<JudicialClassRuleVO> judicialClassRule(@Param("modelId")String modelId, @Param("codeList") List<String> codeList);

    String selectCategoryType(@Param("purposeCategory") String purposeCategory);
}
