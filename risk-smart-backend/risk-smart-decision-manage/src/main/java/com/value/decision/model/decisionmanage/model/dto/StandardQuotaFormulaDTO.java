package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 标准额度公式配置DTO
 *
 * @author Austin
 * @date 2025-11-03
 */
@Data
public class StandardQuotaFormulaDTO {

    /**
     * 额度卡ID
     */
    @NotNull(message = "额度卡ID不能为空")
    private Integer quotaCardId;

    /**
     * 原始公式（前端输入，包含完整字段路径）
     * 示例：MAX(账户信息/账户基本/开户时长(月)*100, 1000) + MIN(账户信息/账户基本/账户等级*500, 5000)
     *
     * 后端会自动将其转换为可执行公式：
     * MAX(account_age*100, 1000) + MIN(account_level*500, 5000)
     */
    @NotBlank(message = "公式不能为空")
    private String rawFormula;

    /**
     * 公式中使用的字段列表
     * 前端拖拽字段时收集，用于后端进行公式转换和字段映射
     */
    @NotEmpty(message = "公式字段列表不能为空")
    private List<FormulaField> formulaFields;

    /**
     * 已选字段的完整树形结构数据（前端传入，用于前端回显）
     * 包含数据源→接口→字段三层结构的完整信息
     * 前端直接使用这个字段进行数据回显和展示
     */
    private List<Object> selectedFields;

    /**
     * 公式字段信息
     */
    @Data
    public static class FormulaField {
        /**
         * 字段代码（用于生成可执行公式）
         * 示例：account_age
         */
        private String fieldCode;

        /**
         * 字段名称（中文）
         * 示例：开户时长
         */
        private String fieldName;

        /**
         * 中台接口编号（用于匹配 data_calling 表的 manageNo）
         */
        private String manageNo;

        /**
         * 数据类型
         * 示例：number, string, decimal
         */
        private String dataType;

        /**
         * 完整路径（用于公式转换）
         * 示例：司法诉讼特征变量/新司法案件明细接口/案号年份T据今相差 特殊值999
         */
        private String fullPath;

        /**
         * 完整路径数组（前端传入，包含三层路径信息）
         * allPaths[0]: 数据源层
         * allPaths[1]: 接口层（包含 interfaceSourceNo + interfaceNo + interfaceManageNo）
         * allPaths[2]: 字段层
         * 用于后端解析提取 sourceNo 和 interfaceNo
         */
        private List<Object> allPaths;
    }
}
