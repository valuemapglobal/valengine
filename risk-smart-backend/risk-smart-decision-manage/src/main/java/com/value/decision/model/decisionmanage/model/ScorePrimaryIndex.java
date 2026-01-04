package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 一级指标存储表
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScorePrimaryIndex extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 一级指标名称
     */
    @NotBlank(message = "指标名称不能为空")
    @Length(max = 30,message =  "指标名称不可超过30")
    private String primaryIndex;

    /**
     * 权重
     */
    @NotNull(message = "权重不能为空")
    @Max(100)
    @Min(0)
    private Double weight;

    /**
     * 描述
     */
    @Length(max =200,message =  "描述不可超过200")
    private String description;

    /**
     * 关联评分卡id
     */
    @NotNull(message = "关联评分卡id不能为空")
    private Integer scoreCardId;

    /**
     * 关联指标规则
     */
    private Integer pointerRuleId;

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
     * 是否叶子节点 0 是  1 否
     */
    private Integer leafNode;

    /**
     * 父指标id
     */
    private Integer parentCardId;

    /**
     * 父指标id集合
     */
    @TableField(exist = false)
    private String parentCardIds;

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

    //指标卡是否存在下级指标卡 0 不存在  1 存在
    @TableField(exist = false)
    private Integer priFlag;
}
