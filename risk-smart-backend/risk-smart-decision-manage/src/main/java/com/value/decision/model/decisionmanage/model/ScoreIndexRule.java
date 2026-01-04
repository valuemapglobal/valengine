package com.value.decision.model.decisionmanage.model;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.value.decision.model.decisionmanage.model.vo.GenerateRuleVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 指标规则表
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreIndexRule extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 指标规则
     */
    @NotNull(message = "指标规则不可为空")
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
    @Length(max =200,message =  "描述不可超过200")
    @NotNull(message = "描述不可为空")
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
    @TableLogic
    private Integer dataState;

    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "左侧产品导航标识不可为空")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景导航标识不可为空")
    private String businessCode;

    /**
     * 策略类型导航标识
     */
    @NotBlank(message = "策略类型导航标识不可为空")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
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
     * 默认指标规则 0 否 1是
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
    @NotNull(message = "指标卡id不可为空")
    private Integer scorePrimaryId;

    /**
     * 评分
     */
    @NotNull(message = "请输入评分")
    @Min(-200)
    @Max(200)
    private Double score;

    /**
     * 评分
     */
    @TableField(exist = false)
    private String scoreInit;

    /**
     * 优先级
     */
    @TableField(exist = false)
    private String levelInit;

    /**
     * 规则
     */
    @TableField(exist = false)
    private GenerateRuleVO generateRuleVO;

    /**
     * 规则回显数据
     */
    private String conditionArray;

    /**
     * 规则组
     */
    @TableField(exist = false)
    private JSONArray conditionArrayJSON;

    //操作功能点
//    @NotBlank(message = "操作功能点不能为空")
    @TableField(exist = false)
    private String operationPoints;

    //策略模型名称
//    @NotNull(message = "策略模型名称不能为空")
    @TableField(exist = false)
    private String policyModelName;

    //功能点标志
//    @NotNull(message = "功能点标志不能为空")
    @TableField(exist = false)
    private String points;

    @NotNull(message = "评分卡id不可为空")
    private Integer scordCardId;

    @NotBlank(message = "指标卡id集不可为空")
    private String scordPrimaryIds;

    private String stronglyReject;

}
