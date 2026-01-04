package com.value.decision.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestDTO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestVO;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.mapper.ProcessPolicyMapper;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.model.ProcessPolicy;
import com.value.decision.process.service.IProcessPolicyService;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程策略表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Slf4j
@Service
public class ProcessPolicyServiceImpl extends ServiceImpl<ProcessPolicyMapper, ProcessPolicy> implements IProcessPolicyService {

    @Autowired
    private ProcessPolicyMapper processPolicyMapper;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    /**
     * 流程策略列表
     * @return
     */
    public List<ProcessPolicy> list(Integer deptId){

//        ProcessPolicy processPolicy = new ProcessPolicy();
        LambdaQueryWrapper<ProcessPolicy> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProcessPolicy::getDeptId,deptId);
        List<ProcessPolicy> processPolicyList = processPolicyMapper.selectList(wrapper);

        if (CollectionUtils.isEmpty(processPolicyList)){
            processPolicyList = new ArrayList<>();
        }

        return processPolicyList;
    }

    /**
     * 获取流程触发的入参
     * @param policyRequestVO
     * @return
     */
    public List<PolicyRequestDTO> getRequestData(PolicyRequestVO policyRequestVO){

        //根据流程ID获取所绑定的模型ID 再根据模型ID获取到所有调用到的数据接口
        LambdaQueryWrapper<ProcessNode> processNodeWrapper = Wrappers.lambdaQuery();
        processNodeWrapper.eq(ProcessNode::getProcessStrategyId,policyRequestVO.getProcessStrategyId())
                .eq(ProcessNode::getDeptId,policyRequestVO.getDeptId())
                .eq(ProcessNode::getDataStatus,0);
        List<ProcessNode> processNodeList = processNodeMapper.selectList(processNodeWrapper);

        //获取所有的manage_no并去重
        List<String> manageSnapshotList = commonRuleFunctionDataNew.getManageSnapshotData(processNodeList);
        log.info("【getRequestData】获取到的manageNo列表: {}", manageSnapshotList);

        //获取所有的入参 目前只能根据中文去重
        List<PolicyRequestDTO> requestDataByManageNo = commonRuleFunctionDataNew.getRequestDataByManageNo(manageSnapshotList);
        log.info("【getRequestData】getRequestDataByManageNo返回数量: {}", requestDataByManageNo == null ? 0 : requestDataByManageNo.size());

        if (CollectionUtils.isNotEmpty(requestDataByManageNo)){
            requestDataByManageNo = requestDataByManageNo.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PolicyRequestDTO::getNameZh))),
                    ArrayList::new));
            log.info("【getRequestData】去重后返回数量: {}", requestDataByManageNo.size());
        }
        return requestDataByManageNo;
    }

}
