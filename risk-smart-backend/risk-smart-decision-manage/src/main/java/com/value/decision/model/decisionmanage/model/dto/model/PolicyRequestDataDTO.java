package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 流程使用页面入参数据
 */

@Data
public class PolicyRequestDataDTO {

    /**
     * 入参数据
     */
    private List<PolicyRequestDTO> policyRequestDTOList;

    /**
     * url链接
     */
    private String url;
}
