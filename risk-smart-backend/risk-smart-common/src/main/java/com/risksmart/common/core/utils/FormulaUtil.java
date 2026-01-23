package com.risksmart.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

/**
 * 公式工具类 - 使用 Apache POI 执行 Excel 公式
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Slf4j
public class FormulaUtil {

    /**
     * 使用 POI 执行 Excel 公式并获取返回值
     *
     * 支持的函数：
     * - 数学函数：SUM, MAX, MIN, ROUND, ABS, POWER, SQRT
     * - 统计函数：AVERAGE, COUNT, MEDIAN
     * - 逻辑函数：IF, AND, OR
     *
     * @param formula Excel 公式（不含等号），例如 "MAX(1,2)+ROUND(3.14,1)"
     * @return 公式计算结果
     * @throws RuntimeException 公式执行失败
     */
    public static double evaluateFormula(String formula) {
        Assert.hasText(formula, "公式不能为空");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("FormulaSheet");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);

            // 设置公式
            cell.setCellFormula(formula);

            // 创建公式计算器
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // 计算公式
            CellValue cellValue = evaluator.evaluate(cell);

            if (cellValue == null) {
                throw new RuntimeException("公式计算结果为空");
            }

            // 返回数值结果
            if (cellValue.getCellType() == CellType.NUMERIC) {
                return cellValue.getNumberValue();
            } else if (cellValue.getCellType() == CellType.BOOLEAN) {
                return cellValue.getBooleanValue() ? 1.0 : 0.0;
            } else if (cellValue.getCellType() == CellType.ERROR) {
                throw new RuntimeException("公式执行错误，错误码：" + cellValue.getErrorValue());
            } else {
                throw new RuntimeException("公式返回了非数值类型：" + cellValue.getCellType());
            }

        } catch (Exception e) {
            log.error("公式计算失败：{}", formula, e);
            throw new RuntimeException("公式计算失败：" + formula, e);
        }
    }

    /**
     * 测试公式是否有效（语法检查）
     *
     * @param formula Excel 公式
     * @return true=有效，false=无效
     */
    public static boolean isValidFormula(String formula) {
        try {
            evaluateFormula(formula);
            return true;
        } catch (Exception e) {
            log.debug("公式校验失败：{}", formula, e);
            return false;
        }
    }

    /**
     * 校验公式并返回详细错误信息（用于前端展示）
     *
     * @param formula Excel 公式（不含等号）
     * @return null=校验通过，非null=错误详情
     */
    public static String validateFormulaWithDetail(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            return "公式不能为空";
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("ValidationSheet");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);

            // 设置公式
            try {
                cell.setCellFormula(formula);
            } catch (Exception e) {
                return parseFormulaException(e, formula);
            }

            // 创建公式计算器
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // 计算公式
            try {
                CellValue cellValue = evaluator.evaluate(cell);

                if (cellValue == null) {
                    return "公式计算结果为空，请检查公式逻辑";
                }

                if (cellValue.getCellType() == CellType.ERROR) {
                    return "公式执行错误，错误码：" + cellValue.getErrorValue();
                }

                // 校验通过
                return null;

            } catch (Exception e) {
                return parseEvaluationException(e, formula);
            }

        } catch (Exception e) {
            log.error("公式校验失败：{}", formula, e);
            return "公式校验失败，请检查公式格式是否正确";
        }
    }

    /**
     * 解析公式设置阶段的异常
     */
    private static String parseFormulaException(Exception e, String formula) {
        String message = e.getMessage();

        // 常见错误类型匹配
        if (message != null) {
            if (message.contains("Parse error")) {
                return "公式语法错误，请检查括号、运算符、函数名是否正确";
            }
            if (message.contains("Too many arguments")) {
                return "函数参数过多，请检查函数调用是否符合规范";
            }
            if (message.contains("Too few arguments")) {
                return "函数参数不足，请检查函数调用是否符合规范";
            }
            if (message.contains("Unknown function")) {
                return "使用了不支持的函数，请仅使用 MAX、MIN、SUM、ROUND、IF、AND、OR 等标准函数";
            }
        }

        log.debug("公式设置异常详情: {}, formula={}", message, formula);
        return "公式格式错误，请检查公式语法是否正确";
    }

    /**
     * 解析公式计算阶段的异常
     */
    private static String parseEvaluationException(Exception e, String formula) {
        String message = e.getMessage();

        if (message != null) {
            if (message.contains("NAME")) {
                return "公式中存在未定义的名称或字段代码，请检查所有字段是否已配置";
            }
            if (message.contains("VALUE")) {
                return "公式中存在类型错误，请检查字段类型是否与运算匹配";
            }
            if (message.contains("DIV")) {
                return "公式中存在除零错误，请检查分母是否可能为 0";
            }
            if (message.contains("REF")) {
                return "公式中存在引用错误，请检查字段路径是否正确";
            }
            if (message.contains("MissingArgEval") || message.contains("Missing") || message.contains("argument")) {
                return "公式参数缺失或不完整，请检查函数的参数是否正确（例如：逗号后不能直接跟右括号等）";
            }
        }

        log.debug("公式执行错误详情: {}, formula={}", message, formula);
        return "公式格式错误，请检查公式语法是否正确";
    }
}
