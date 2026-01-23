package com.value.decision.process.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.process.mapper.ProcessPolicyTaskMapper;
import com.value.decision.process.model.ProcessPolicyTask;
import com.value.decision.process.service.IProcessPolicyTaskService;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程策略任务表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Service
public class ProcessPolicyTaskServiceImpl extends ServiceImpl<ProcessPolicyTaskMapper, ProcessPolicyTask> implements IProcessPolicyTaskService {

    @Resource
    private ProcessPolicyTaskMapper processPolicyTaskMapper;

    @Override
    public List<Map<String, Object>> getTask(String approvalUserName, String product, String businessCode, String startTime, String endTime, Integer i, Integer id) {
        return processPolicyTaskMapper.getTask(approvalUserName,product,businessCode,startTime,endTime,i,id);
    }

    @Override
    public List<Map<String, Object>> getNodeResult(Integer processStrategyId) {
        return processPolicyTaskMapper.getNodeResult(processStrategyId);
    }

    /**
     * 额度任务回调接口
     */
    public ProcesPolicyDTO limitTask(ProcesPolicyVO procesPolicyVO){

        return processPolicyTaskMapper.limitTask(procesPolicyVO);
    }

    @Override
    public int getApprovalTaskCount(Integer id) {
        return processPolicyTaskMapper.getApprovalTaskCount(id);
    }

    @Override
    public List<Map<String, Object>> getTaskList(String approvalUserName, String idNumber, String product, String businessCode, String startTime, String endTime, Integer status,String orderNo,String lccl,String mxmc,String status2,String deptId,String customerName,String evaluationResult) {
        return processPolicyTaskMapper.getTaskList(approvalUserName,idNumber,product,businessCode,startTime,endTime,status,orderNo,lccl,mxmc,status2,deptId,customerName,evaluationResult);
    }

    @Override
    public List<Map<String, Object>> getTaskListByField(String field,String remark,List<String> dealBankNameList) {
        return processPolicyTaskMapper.getTaskListByField(field,remark,dealBankNameList);
    }

}
