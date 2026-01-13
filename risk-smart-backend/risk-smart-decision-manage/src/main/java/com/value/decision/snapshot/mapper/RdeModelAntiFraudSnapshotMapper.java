package com.value.decision.snapshot.mapper;

import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.risksmart.common.core.base.CommonMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 反欺诈模型表 Mapper 接口
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
public interface RdeModelAntiFraudSnapshotMapper extends CommonMapper<RdeModelAntiFraudSnapshot> {
    @Select("select * from rde_model_anti_fraud_snapshot where id=#{modelId} and data_status=0")
    RdeModelAntiFraudSnapshot getById(Integer modelId);

    //检验此部门下是否有模型已发布过
    public List<RdeModelAntiFraudSnapshot> queryList(@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    //检验此部门下是否有模型已发布过
    public List<RdeModelAntiFraudSnapshot> queryList2(@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);
    //清除快照表已存在的数据
    public void deleteList(@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    public void deleteList2(@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);
}
