package com.value.decision.version.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 一级指标存储表
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScorePrimaryIndexVersion extends Model {

    private Integer id;

    /**
     * 一级指标名称
     */
    private String primaryIndex;

    /**
     * 权重
     */
    private Double weight;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联评分卡id
     */
    private Integer scoreCardId;

    /**
     * 关联指标规则
     */
    private Integer pointerRuleId;

    /**
     * 数据状态 0正常 1删除
     */
    private Integer dataState;

    /**
     * 左侧产品导航标识
     */
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    private String ruleCode;

    /**
     * 部门标识 1超级管理员 2普通用户
     */
    private Integer deptFlag;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 按钮状态 1启用 0禁用
     */
    private Integer buttonState;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 操作人ID
     */
    private Integer createUserId;

    /**
     * 版本控制
     */
    private String versionControl;

    /**
     * 父指标id
     */
    private Integer parentCardId;

    /**
     * 是否叶子节点 0 是 1否
     */
    private Integer leafNode;


}
