package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 模型任务记录表
 * </p>
 *
 * @author dianne
 * @since 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_task_record")
public class ModelTaskRecord extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 批次ID(关联批次表)
     */
    private Long batchId;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

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
     * 总分
     */
    private BigDecimal  totalScore;

}
