package com.value.decision.model.rdenew.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleGroupVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RdeModelAntiFraudRuleGroupMapper extends CommonMapper<RdeModelAntiFraudRuleGroup> {
    @Select("select id,name,descr,dept_id\n" +
            ",(select case when count(1)>0 THEN 1 else 0 END from rde_model_anti_project_fraud where  rde_model_anti_project_fraud.group_id = rde_model_anti_fraud_rule_group.id) as status\n" +
            ",(select rde_model_anti_project_fraud.create_time from rde_model_anti_project_fraud where  rde_model_anti_project_fraud.group_id = rde_model_anti_fraud_rule_group.id order by rde_model_anti_project_fraud.create_time desc limit 1) as stime\n" +
            ",case when dept_id = 101 THEN 1 else 0 end as flag\n" +
            "from rde_model_anti_fraud_rule_group \n" +
            "WHERE  data_status=0  \n" +
            "order by stime desc")
    List<RdeModelAntiFraudRuleGroupVO> selectNewList (Integer modelId);
    
    List<RdeModelAntiFraudRuleGroupVO> newList (@Param("modelId")Integer modelId,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

    @Select("select id,model_id,name,descr,dept_id\n" +
            "        ,status\n" +
            "        ,update_time\n" +
            "        ,dept_flag\n" +
            "        from rde_model_anti_fraud_rule_group where data_status=0   and model_id = #{modelId} and dept_id = #{deptId} and project_code = #{projectCode} and business_code = #{businessCode} and rule_code = #{ruleCode}")
    List<RdeModelAntiFraudRuleGroupVO> newList2 (@Param("modelId")Integer modelId,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("businessCode") String businessCode,@Param("ruleCode") String ruleCode);
    //从原表中获取数据存入快照表 -- 策略组表
    public List<RdeModelAntiFraudRuleGroup> queryList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode);
    //从原表中获取数据存入快照表 -- 策略组表
    public List<RdeModelAntiFraudRuleGroup> queryList2(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode
            ,@Param("businessCode") String businessCode,@Param("ruleCode") String ruleCode);

    @Select("select * from rde_model_anti_fraud_rule_group where model_id=#{modelId} and dept_id=#{deptId} and data_status=0 and project_code=#{projectCode}")
    public List<RdeModelAntiFraudRuleGroup> queryListByModelId(@Param("modelId") Integer modelId, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

    @Select("select * from rde_model_anti_fraud_rule_group where data_status=0 and dept_id=#{deptId} and dept_flag=1")
    public List<RdeModelAntiFraudRuleGroup> queryByDeptId(@Param("deptId") Integer deptId);

    @Select("select * from rde_model_anti_fraud_rule_group where data_status=0 and model_id=#{oldModelId} and dept_id=#{deptId} and dept_flag=1 and project_code=#{projectCode}")
    public List<RdeModelAntiFraudRuleGroup> queryByOldModelId(@Param("oldModelId") Integer oldModelId,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

    //从原表中删除其他账号下的标准模型数据 -- 策略组表
    public void deleteList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    void updateList(@Param("groupIdList") List<String> groupIdList,@Param("deptId") Integer deptId);

    List<Integer> selectDeleted(@Param("modelIdList") List<Integer> modelIdList,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    //批量修改
    void updateVersionList(RdeModelAntiFraudRuleGroup fraudList);

    //批量插入
    public void insertBatch(@Param("fraudGroupList") List<RdeModelAntiFraudRuleGroup> fraudGroupList);

    /**
     * 根据规则组ID查询其关联的模型ID(策略ID)
     * @param groupId 规则组ID
     * @return 模型ID
     */
    @Select("select model_id from rde_model_anti_fraud_rule_group where id=#{groupId} and data_status=0")
    Integer selectModelIdByGroupId(@Param("groupId") Integer groupId);
}