package com.value.decision.process.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 流程节点表
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessNode extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 流程策略表id
     */
    private Integer processStrategyId;

    /**
     * 策略导航标识 评分卡id  规则id 评级id 额度id 定价id
     */
    private String ruleCode;

    /**
     * 节点标识  节点排序  1 2 3 4
     */
    private Integer nodeId;

    /**
     * 模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
     */
    private Integer moduleId;

    /**
     * 审批结果
     */
//    private String approvalResult;

    /**
     * 审批意见
     */
//    private String approvalContent;

    /**
     * 节点结果详情
     */
//    private String nodeDetail;

    /**
     * 审批时间
     */
//    private LocalDateTime approvalTime;

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

    @TableLogic
    private Integer dataStatus;

    //模型名称 评分卡名称 评级卡名称 额度卡名称 定价卡名称
    private String ruleName;


}
