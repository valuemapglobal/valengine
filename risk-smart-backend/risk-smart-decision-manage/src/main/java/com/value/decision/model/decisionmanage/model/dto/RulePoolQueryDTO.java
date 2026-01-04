package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 规则池查询DTO
 * @author Claude Code
 * @date 2025-12-01
 */
@Data
public class RulePoolQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    // ========== 导出用：选中的规则ID列表 ==========
    // 如果不为空，按选中的ID导出；否则按筛选条件导出
    private List<Long> ids;

    // ========== 搜索条件 ==========
    private String keyword;

    // ========== 基础筛选 ==========
    // 状态（0-已禁用，1-启用中）
    private String status;

    // 风险等级（1-低风险，2-中低风险，3-中风险，4-中高风险，5-高风险）
    private String riskLevel;

    private String strategy;

    // 命中动作（拒绝/转人工）
    private String hitAction;

    // ========== 高级筛选 ==========
    private String businessScene;

    // 模型类型（1-评分，5-规则，6-分类）
    private String modelType;

    private String referenceProduct;

    private String createTimeStart;

    private String createTimeEnd;
}
