package com.value.decision.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.process.model.ProcessPolicyTask;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程策略任务表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface IProcessPolicyTaskService extends IService<ProcessPolicyTask> {


    List<Map<String,Object>> getTask(String approvalUserName, String product, String businessCode, String startTime, String endTime, Integer i, Integer id);

    List<Map<String,Object>> getNodeResult(Integer processStrategyId);

    /**
     * 额度任务回调接口
     */
    public ProcesPolicyDTO limitTask(ProcesPolicyVO procesPolicyVO);

    int getApprovalTaskCount(Integer id);

    List<Map<String,Object>> getTaskList(String approvalUserName, String idNumber, String product, String businessCode, String startTime,
                                         String endTime,Integer status,String orderNo,String lccl,String mxmc,String status2,String deptId
    ,String customerName,String evaluationResult);

    List<Map<String,Object>> getTaskListByField(String field,String remark,List<String> dealBankNameList);
}
