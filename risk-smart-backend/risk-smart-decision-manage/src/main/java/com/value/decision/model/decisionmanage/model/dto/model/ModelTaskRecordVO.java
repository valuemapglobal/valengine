package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ModelTaskRecordVO {

    /**
     * 流程ID
     */
    private Integer processId;

    /**
     * 流程策略名称
     */
    private String processStrategy;

    /**
     * 响应形式 1数据 2报告
     */
    private Integer responseForm;

    /**
     * 入参数据
     */
    private Map<String,Object> processEntry;


    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 申请用户账号
     */
    private String applicationUser;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 详情入参
     */
    private List<PolicyRequestDTO> policyRequestList;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 流程任务状态
     */
    private Integer taskStatus;

    /**
     * 用户信息
     */
    Map<String,Object> userIdentity;

    private Integer pageNum;
    private Integer pageSize;
    /**
     * 模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
     */
    private Integer moduleId;

    /**
     * 用户标识
     */
    private String userIdentification;

    /**
     * 批次ID(用于查询某个批次下的所有任务)
     */
    private Long batchId;
}
