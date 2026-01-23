package com.value.decision.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestDTO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestVO;
import com.value.decision.process.model.ProcessPolicy;

import java.util.List;

/**
 * <p>
 * 流程策略表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
public interface IProcessPolicyService extends IService<ProcessPolicy> {

    /**
     * 流程策略列表
     */
    List<ProcessPolicy> list(Integer deptId);

    List<PolicyRequestDTO> getRequestData(PolicyRequestVO policyRequestVO);

}
