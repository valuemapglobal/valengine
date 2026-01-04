package com.value.decision.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.process.model.ModuleRuleResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 模块任务结果表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface ModuleRuleResultMapper extends BaseMapper<ModuleRuleResult> {

    @Insert("INSERT INTO rule_business ( `businessCode`, `detail`,`modelNo`,`userName`,`phone`,`idNumber`,`enterpriseName`,`creditCode`,`ckey`,`interfaceType`,`businessType`,`modelName`,`subject`,`createTime`) " +
            "VALUES ( #{businessCode}, #{detail}, #{modelNo} , #{userName}, #{phone}, #{idNumber}, #{enterpriseName}, #{creditCode} , #{ckey}, #{interfaceType}, #{businessType}, #{modelName}, #{subject}, #{createTime}) ")
    void insertBusiness(@Param("businessCode") String businessCode, @Param("detail")  String detail, @Param("modelNo")  String modelNo
                        , @Param("userName")  String userName, @Param("phone")  String phone, @Param("idNumber")  String idNumber, @Param("enterpriseName")  String enterpriseName
            , @Param("creditCode")  String creditCode, @Param("ckey") String ckey, @Param("interfaceType") Integer interfaceType, @Param("businessType") Integer businessType, @Param("modelName") String modelName, @Param("subject") String subject, @Param("createTime")LocalDateTime createTime);

    @Select("select count(1) from rule_business where businessCode = #{businessCode} ")
    int checkBusiness(@Param("businessCode") String businessCode);

    @Select("select `userName`,`phone`,`idNumber`,`enterpriseName`,`creditCode`,`ckey`,`interfaceType`,`modelNo` from rule_business where businessCode = #{orderNo}")
    Map<String,Object> getDetailByOrderNo(@Param("orderNo")String orderNo);

    @Select("select `userName`,phone mobile, idNumber idNumber,`enterpriseName`,`businessType`,`subject` from rule_business where businessCode = #{orderNo}")
    Map<String,Object> getDetailByOrderNoV2(@Param("orderNo")String orderNo);

    @Select("SELECT p.process_strategy FROM `rule_business` b inner join process_policy p on b.modelName = p.id \n" +
            "where b.businessCode = #{orderNo} ")
    String getProcessNameByOrderNo(@Param("orderNo")String orderNo);

    @Select("SELECT interfaceType, modelApplication, callStatus, responseBody FROM `data_calling` WHERE orderNo = #{orderNo} GROUP BY interfaceType, modelApplication, callStatus, responseBody")
    List<Map<String,Object>> getDataCellingByOrderNo(@Param("orderNo")String orderNo);

    @Select("select count(1) from process_policy_task where task_number = #{taskNumber} ")
    int checkBusinessInProcessPolicyTask(@Param("taskNumber") String taskNumber);
}
