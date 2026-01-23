package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 规则池月度快照表
 * 用于统计环比数据
 *
 * @author Claude Code
 * @date 2025-12-02
 */
@Data
@TableName("rule_pool_monthly_snapshot")
public class RulePoolMonthlySnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 快照月份，格式：yyyy-MM
     */
    private String snapshotMonth;

    /**
     * 规则ID
     */
    private Integer ruleId;

    /**
     * 当时状态（0-禁用，1-启用）
     */
    private String status;

    /**
     * 产品编码
     */
    private String projectCode;

    /**
     * 业务场景编码
     */
    private String businessCode;

    /**
     * 模型类型编码
     */
    private String ruleCode;

    /**
     * 快照创建时间
     */
    private LocalDateTime createTime;
}
