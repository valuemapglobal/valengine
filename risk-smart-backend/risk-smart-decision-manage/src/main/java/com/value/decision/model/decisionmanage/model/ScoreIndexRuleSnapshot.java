package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 指标规则表
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreIndexRuleSnapshot extends Model {

    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 指标规则
     */
    private String indexRule;

    /**
     * 数据模块
     */
    private String dataModule;

    /**
     * code
     */
    private String code;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 决策组
     */
    private String decisionGroup;

    /**
     * 描述
     */
    private String description;

    /**
     * 规则所用的包
     */
    private String termPackage;

    /**
     * 处理所需别名
     */
    private String termKey;

    /**
     * 生成规则
     */
    private String termRule;

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
     * 默认指标规则  0 否 1是
     */
    private Integer defaultRule;

    /**
     * 是否生效 0 否 1是
     */
    @TableField("takeEffect")
    private Integer takeEffect;

    /**
     * 指标卡id
     */
    private Integer scorePrimaryId;

    /**
     * 评分
     */
    private Double score;

    @NotNull(message = "评分卡id不可为空")
    private Integer scordCardId;

    @NotBlank(message = "指标卡id集不可为空")
    private String scordPrimaryIds;

    @NotBlank(message = "是否强拒绝不可为空")
    private String stronglyReject;

    /**
     * 规则回显数据
     */
    private String conditionArray;
}
