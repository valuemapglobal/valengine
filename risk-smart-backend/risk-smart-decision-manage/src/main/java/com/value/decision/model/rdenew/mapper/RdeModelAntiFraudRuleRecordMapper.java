package com.value.decision.model.rdenew.mapper;

import com.value.decision.common.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RdeModelAntiFraudRuleRecordMapper extends CommonMapper<RdeModelAntiFraudRuleRecord> {

    List<RdeModelAntiFraudRuleRecordVO> newList(@Param("groupId") Integer groupId, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode);

    //从原表中获取数据存入快照表 -- 策略表
    public List<RdeModelAntiFraudRuleRecord> queryList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode);

    //从原表中获取数据存入快照表 -- 策略表
    public List<RdeModelAntiFraudRuleRecord> queryList2(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode
            , @Param("businessCode") String businessCode, @Param("ruleCode") String ruleCode);

    @Select("select * from rde_model_anti_fraud_rule_record where group_id=#{groupId} and dept_id=#{deptId} and data_status=0 and project_code = #{projectCode}")
    public List<RdeModelAntiFraudRuleRecord> queryListByGroupId(@Param("groupId") Integer groupId, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode);

    @Select("select * from rde_model_anti_fraud_rule_record where data_status=0 and dept_id=#{deptId}  and dept_flag=1")
    public List<RdeModelAntiFraudRuleRecord> queryByDeptId(@Param("deptId") Integer deptId);

    @Select("select * from rde_model_anti_fraud_rule_record where data_status=0 and group_id=#{groupId} and dept_id=#{deptId} and dept_flag=1 and project_code = #{projectCode}")
    public List<RdeModelAntiFraudRuleRecord> queryByGroupId(@Param("groupId") Integer groupId, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode);

    //从原表中删除其他账号下的标准模型数据 -- 策略表
    public void deleteList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode, @Param("ruleCode") String ruleCode);


    @Delete("delete from rde_model_anti_fraud_rule_record where model_id=#{modelId} and group_id=#{groupId}")
    void deleteByGroupAndModule(@Param("modelId") Integer modelId, @Param("groupId") Integer groupId);

    @Select("select id,name,descr,dept_id\n" +
            ",(select case when count(1)>0 THEN 1 else 0 END from rde_model_anti_project_fraud where  rde_model_anti_project_fraud.rule_id = rde_model_anti_fraud_rule_record.id) as status\n" +
            ",(select rde_model_anti_project_fraud.create_time from rde_model_anti_project_fraud where  rde_model_anti_project_fraud.rule_id = rde_model_anti_fraud_rule_record.id order by rde_model_anti_project_fraud.create_time desc limit 1) as stime\n" +
            ",case when dept_id = 101 THEN 1 else 0 end as flag\n" +
            "from rde_model_anti_fraud_rule_record \n" +
            "WHERE  data_status=0\n" +
            "order by stime desc")
    List<RdeModelAntiFraudRuleRecordVO> selectNewList();

    void updateList(@Param("recordIdList") List<String> recordIdList, @Param("deptId") Integer deptId);

    List<Integer> getCodeIdDeleted(@Param("groupIdList") List<Integer> groupIdList, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode, @Param("ruleCode") String ruleCode);

    List<RdeModelDecisionCodeLevel> newList2(@Param("groupId") Integer groupId);

    //批量修改
    void updateVersionList(RdeModelAntiFraudRuleRecord fraudList);

    //批量插入
    public void insertBatch(@Param("fraudRecordList") List<RdeModelAntiFraudRuleRecord> fraudRecordList);

    /**
     * 根据规则ID查询其关联的模型ID(策略ID)
     * @param ruleId 规则ID
     * @return 模型ID
     */
    @Select("select model_id from rde_model_anti_fraud_rule_record where id=#{ruleId} and data_status=0")
    Integer selectModelIdByRuleId(@Param("ruleId") Integer ruleId);
}