package com.value.decision.process.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 流程策略节点结果表 
 * </p>
 *
 * @author hc
 * @since 2023-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessNodeResult extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 流程策略 id
     */
    private Integer processId;

    /**
     * 策略节点 id
     */
    private Integer processNodeId;

    /**
     * 审批结果
     */
    private String result;

    /**
     * 审批意见
     */
    private String opinion;

    /**
     * 节点结果详情
     */
    private String detail;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime approvalTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 审批人ID
     */
    private Integer approvalUserId;

    /**
     * 审批人名称
     */
    private String approvalName;

    /**
     * 审批状态 审批状态 -1 无需审核 0 待审核 1 审核通过 2审核拒绝
     */
    private Integer approvalStatus;

    //策略导航标识
    private String ruleCode;

    //返还结果
    private String returnData;

    //节点标识
    private Integer nodeId;

    //策略导航名称
    private String ruleName;

    /**
     * 模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
     */
    private Integer moduleId;

    //任务id
    private Integer taskId;


}
