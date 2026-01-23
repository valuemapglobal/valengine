package com.value.decision.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.process.model.ProcessPolicyTask;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程策略任务表 Mapper 接口
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface ProcessPolicyTaskMapper extends BaseMapper<ProcessPolicyTask> {


    List<Map<String, Object>> getTask(@Param("approvalUserName") String approvalUserName, @Param("product")  String product, @Param("businessCode")  String businessCode, @Param("startTime")  String startTime, @Param("endTime")  String endTime, @Param("status")  Integer status, @Param("approvalUserId")  Integer approvalUserId);

    List<Map<String, Object>> getNodeResult(@Param("processStrategyId")Integer processStrategyId);
    /**
     * 额度任务回调接口
     */
    public ProcesPolicyDTO limitTask(ProcesPolicyVO procesPolicyVO);

    int getApprovalTaskCount(@Param("processStrategyId")Integer processStrategyId);

    List<Map<String, Object>> getTaskList(@Param("approvalUserName")String approvalUserName,@Param("idNumber")String idNumber,@Param("product") String product
            ,@Param("businessCode") String businessCode,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("status") Integer status,@Param("orderNo")String orderNo
            ,@Param("lccl")String lccl,@Param("mxmc")String mxmc,@Param("status2")String status2,@Param("deptId")String deptId,@Param("customerName")String customerName,@Param("evaluationResult")String evaluationResult);

    List<Map<String, Object>> getTaskListByField(@Param("field")String field,@Param("remark")String remark,@Param("dealBankNameList")List<String> dealBankNameList);
}
