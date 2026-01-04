package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>
 * 评级范围表
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RateCardRadius extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联评级卡id
     */
    @NotNull(message = "评级卡id不可为空")
    private Integer rateCardId;

    /**
     * 父id
     */
    private Integer parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 标准评级
     */
    private String standardRate;

    /**
     * 标准额度测算来源
     */
    private String creditSource;

    /**
     * 评分范围
     */
    private String rateRange;

    /**
     * 评分范围说明
     */
    private String rateRangeContent;

    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "产品导航标识不可为空")
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

    @TableField(exist = false)
    @NotNull(message = "评分范围不可为空")
    private List<Map<String,String>> mapList;

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


}
