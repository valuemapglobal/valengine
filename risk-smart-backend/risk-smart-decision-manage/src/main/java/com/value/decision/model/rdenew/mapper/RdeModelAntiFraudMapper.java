package com.value.decision.model.rdenew.mapper;

import com.risksmart.common.core.base.CommonMapper;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiProjectFraud;
import com.value.decision.model.rdenew.vo.RdeUpdateStateEntryVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RdeModelAntiFraudMapper extends CommonMapper<RdeModelAntiFraud> {

    @Select("SELECT * FROM `rde_model_anti_fraud` WHERE name = #{name} AND dept_id = #{deptId} AND project_code = #{projectCode} AND business_code = #{businessCode} AND data_status = #{dataStatus}")
    RdeModelAntiFraud select(@Param("versionControl") String versionControl,@Param("projectCode") String projectCode,@Param("businessCode") String businessCode,@Param("name") String name,@Param("deptId") Integer deptId,@Param("dataStatus") Integer dataStatus);

    @Select("select id,name,descr,version_control from rde_model_anti_fraud where id=#{modelId}")
    RdeModelAntiFraud selectById(@Param("modelId") Integer modelId);

    @Select("select * from rde_model_anti_fraud where id=#{modelId}")
    RdeModelAntiFraud selectRecord(@Param("modelId") Integer modelId);


    @Update("UPDATE rde_model_anti_fraud rmaf SET rmaf.data_status=#{dataStatus} WHERE id=#{id}")
    RdeModelAntiFraud updateDateStatusById(@Param("dataStatus") Byte dataStatus,@Param("id") Integer id);

    @Update("UPDATE rde_model_anti_project_fraud SET rde_model_anti_project_fraud.model_id=#{model_id} WHERE rde_model_anti_project_fraud.model_id = rde_model_anti_fraud.id")
    RdeModelAntiProjectFraud updateModelIdById(@Param("modelId") Integer modelId);

    /*@Select("select id,name,descr,dept_id\n" +
            "     ,(select case when sum(1)>0 THEN 1 else 0 END from rde_model_anti_project_fraud where project_code=#{projectCode} and rde_model_anti_project_fraud.model_id = rde_model_anti_fraud.id and rde_model_anti_project_fraud.dept_id=#{deptId}) as status\n" +
            "     ,(select rde_model_anti_project_fraud.create_time from rde_model_anti_project_fraud where project_code=#{projectCode} and rde_model_anti_project_fraud.model_id = rde_model_anti_fraud.id order by rde_model_anti_project_fraud.create_time desc limit 1) as stime\n" +
            "     ,case when dept_id = 101 THEN 1 else 0 end as flag" +
            "     ,case when dept_id = 101 THEN 1 else 2 end as deptFlag\n" +
            "     from rde_model_anti_fraud\n" +
            "     WHERE status = 1 and  model_type =#{modelType} and data_status=0\n" +
            "     and dept_id =#{deptId} \n" +
            "     order by stime desc")*/
    @Select("select id,name,descr,dept_id\n" +
            ",status\n" +
            ",update_time\n" +
            ",dept_flag\n" +
            " from rde_model_anti_fraud\n" +
            "WHERE model_type =#{modelType} and data_status= 0 \n" +
            "and dept_id =#{deptId} and project_code=#{projectCode} and rule_code=#{ruleCode} \n" +
            "order by update_time desc")
    //                                                          @Param("projectCode") String projectCode,
    List<RdeModelAntiFraud> selectByprojectCodeAndByModelType(@Param("modelType") String modelType, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    @Select("select id,name,descr,dept_id\n" +
            ",status\n" +
            ",update_time\n" +
            ",dept_flag\n" +
            " from rde_model_anti_fraud\n" +
            "WHERE model_type =#{modelType} and data_status= 0 \n" +
            "and dept_id =#{deptId} and project_code=#{projectCode} and rule_code=#{ruleCode}  and business_code = #{businessCode}\n" +
            "order by update_time desc")
        //                                                          @Param("projectCode") String projectCode,
    List<RdeModelAntiFraud> selectByprojectCodeAndByModelType2(@Param("modelType") String modelType, @Param("deptId") Integer deptId, @Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);

    //从原表中获取数据存入快照表 -- 模型表
    public List<RdeModelAntiFraud> queryList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);
    //从原表中获取数据存入快照表 -- 模型表
    public List<RdeModelAntiFraud> queryList2(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode,@Param("businessCode") String businessCode);

    @Select("select * from rde_model_anti_fraud where data_status=0 and dept_id=#{deptId} and id=#{id} and project_code=#{projectCode}")
    public RdeModelAntiFraud queryById(@Param("id") Integer id, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

    @Select("select * from rde_model_anti_fraud where data_status=0 and name=#{modelName} and dept_id=#{deptId} and project_code=#{projectCode}")
    public RdeModelAntiFraud queryByDeptId(@Param("modelName") String modelName ,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode);

    @Select("select * from rde_model_anti_fraud where dept_id=#{deptId} and dept_flag=1 and data_status=0  and dept_flag=1")
    List<RdeModelAntiFraud> queryStandardListByDeptId(Integer deptId);

    //从原表中删除其他账号下的标准模型数据 -- 模型表
    public void deleteList(@Param("ids") List<Integer> ids, @Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);

    void updateStatus(RdeUpdateStateEntryVO rdeUpdateStateEntryVO);

    void updateList(@Param("modelIdList") List<String> modelIdList,@Param("deptId") Integer deptId);

    List<Integer> selectDeleted(@Param("modelIdList") List<Integer> modelIdList,@Param("deptId") Integer deptId,@Param("projectCode") String projectCode,@Param("ruleCode") String ruleCode);


    //批量修改
    void updateVersionList(RdeModelAntiFraud fraudList);

    //批量插入
    public void insertBatch(@Param("fraudList") List<RdeModelAntiFraud> fraudList);
}