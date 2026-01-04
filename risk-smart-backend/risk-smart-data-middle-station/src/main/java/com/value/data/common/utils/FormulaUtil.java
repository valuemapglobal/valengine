package com.value.data.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

/**
 * @author Vida
 * @date 2025年04月23日 16:25
 * @description 
 */
public class FormulaUtil {

    /**
     * 使用POI执行Excel公式并获取返回值
     *
     * @param formula Excel公式，例如 "max(1,2)+1"
     * @return 公式计算结果
     */
    public static double evaluateFormula(String formula) {
        Assert.hasText(formula, "公式不能为空");
        // 创建一个空的Excel工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建一个工作表
            Sheet sheet = workbook.createSheet("FormulaSheet");
            // 创建一行
            Row row = sheet.createRow(0);
            // 创建一个单元格
            Cell cell = row.createCell(0);
            // 设置单元格公式
            cell.setCellFormula(formula);
            // 创建公式计算器
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            // 计算公式
            CellValue cellValue = evaluator.evaluate(cell);
            // 返回计算结果
            return cellValue.getNumberValue();
        } catch (Exception e) {
            throw new RuntimeException("公式计算失败："+formula , e);
        }
    }

}