package com.value.decision.model.decisionmanage.model.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 规则池导出DTO
 * @author Claude Code
 * @date 2025-12-01
 */
@Data
public class RulePoolExportDTO {

    @ExcelProperty(value = "策略", index = 0)
    @ColumnWidth(20)
    private String strategy;

    @ExcelProperty(value = "规则组", index = 1)
    @ColumnWidth(20)
    private String ruleGroup;

    @ExcelProperty(value = "规则CODE", index = 2)
    @ColumnWidth(25)
    private String ruleCode;

    @ExcelProperty(value = "规则描述", index = 3)
    @ColumnWidth(40)
    private String ruleDesc;

    @ExcelProperty(value = "风险等级", index = 4)
    @ColumnWidth(12)
    private String riskLevel;

    @ExcelProperty(value = "命中动作", index = 5)
    @ColumnWidth(15)
    private String hitAction;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(10)
    private String status;

    @ExcelProperty(value = "参考产品", index = 7)
    @ColumnWidth(20)
    private String referenceProduct;

    @ExcelProperty(value = "业务场景", index = 8)
    @ColumnWidth(15)
    private String businessScene;

    @ExcelProperty(value = "模型类型", index = 9)
    @ColumnWidth(12)
    private String modelType;

    @ExcelProperty(value = "创建时间", index = 10)
    @ColumnWidth(20)
    private String createTime;
}
