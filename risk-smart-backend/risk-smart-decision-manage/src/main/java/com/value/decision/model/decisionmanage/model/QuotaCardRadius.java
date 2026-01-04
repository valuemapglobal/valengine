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
 * 额度范围表
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaCardRadius extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联额度卡id
     */
    @NotNull(message = "额度卡id不可为空")
    private Integer quotaCardId;

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
    @NotBlank(message = "标准评级不可为空")
    private String standardRate;

    /**
     * 标准额度测算来源
     * 注释掉校验: 默认使用数据中台的creditFacility(授信额度)作为数据来源
     */
//    @NotBlank(message = "标准额度测算来源不可为空")
    private String creditSource;

    /**
     * 评级
     */
    private String standardName;

    /**
     * 额度浮动范围
     */
    private String quotaRange;

    /**
     * 额度浮动范围说明
     */
    private String quotaRangeContent;

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
}
