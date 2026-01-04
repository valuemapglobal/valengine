package com.value.decision.model.decisionmanage.model.dto.batch;

import lombok.Data;

/**
 * 批次查询DTO
 *
 * @author Claude
 * @since 2025-01-14
 */
@Data
public class BatchQueryDTO {

    /**
     * 批次号(模糊查询)
     */
    private String batchNo;

    /**
     * 流程策略ID
     */
    private Integer processId;

    /**
     * 流程策略名称(模糊查询)
     */
    private String processStrategy;

    /**
     * 批次状态: 1待处理,2处理中,3全部成功,4部分失败,5全部失败,6校验失败
     */
    private Integer batchStatus;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 创建用户名(模糊查询)
     */
    private String userName;

    /**
     * 响应形式 1数据 2报告
     */
    private Integer responseForm;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
