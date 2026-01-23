package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * 额度卡主表
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuotaCardRecordSnapshot extends Model {

    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 关联评级卡id
     */
    private Integer rateCardId;

    /**
     * 额度模型名称(对应评级卡名称)
     */
    private String quotaCard;

    /**
     * 数据状态
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
     * 描述
     */
    private String description;

    // ==================== 标准额度计算公式配置（快照表同步字段）====================

    /**
     * Excel计算公式
     */
    private String standardQuotaFormula;

    /**
     * 中文公式（展示用）
     */
    private String standardQuotaFormulaZh;

    /**
     * 公式变量映射JSON（字符串格式）
     */
    private String formulaVariables;

    /**
     * 已选字段列表JSON（字符串格式）
     */
    private String selectedFields;

}
