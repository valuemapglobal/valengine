package com.value.decision.snapshot.mapper;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.risksmart.common.core.base.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反欺诈模型规则组表 Mapper 接口
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface RdeModelAntiFraudRuleGroupSnapshotMapper extends CommonMapper<RdeModelAntiFraudRuleGroupSnapshot> {

    //清除快照表已存在的数据
    public void deleteList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode") String ruleCode);

    //清除快照表已存在的数据
    public void deleteList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);

    //根据模型id查询groupId
    public List<Integer> queryList(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode") String ruleCode);

    //根据模型id查询groupId
    public List<Integer> queryList2(@Param("deptId") Integer deptId,@Param("projectCode")String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);
}
