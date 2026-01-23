package com.value.decision.model.decisionmanage.model.dto.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TaskRecordDTO {

    private Integer id;

    /**
     * 任务号
     */
    private String taskNo;

    /**
     * 申请用户
     */
    private String applicationUser;

    /**
     * 流程入参
     */
    private String processEntry;

    /**
     * 业务场景
     */
    private Integer businessCode;

    /**
     * 流程策略
     */
    private String processStrategy;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型名称列表
     */
    private List<String> modelNameList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 响应形式 1数据 2报告
     */
    private Integer responseForm;

    /**
     * 任务状态 1初始化,2生成中,3生成成功,4生成失败
     */
    private Integer taskStatus;

    /**
     * 数据状态 默认0 删除1
     */
    private Integer dataStatus;

    /**
     * 流程ID
     */
    private Integer processId;

    /**
     * 流程运行drl脚本
     */
    private String termRule;

    /**
     * 任务执行时的流程节点快照 (JSON数组)
     */
    private String processNodesSnapshot;

    /**
     * 失败原因 (仅在taskStatus=4时有值，用户友好的错误提示)
     */
    private String failureMessage;
}
