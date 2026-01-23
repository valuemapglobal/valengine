package com.value.decision.process.vo;

import lombok.Data;

/**
 * 额度任务回调返回值
 */

@Data
public class ProcesPolicyDTO {

    /**
     * 任务编号
     */
    private String taskNumber;

    /**
     * 流程策略模型名称
     */
    private String processStrategy;

    /**
     * 审批结果
     */
    private String result;

    /**
     * 节点结果详情
     */
    private String detail = "0";

    /**
     * 任务状态 0生成中 1通过 2拒绝
     */
    private Integer flag;
}
