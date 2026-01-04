package com.value.decision.model.decisionmanage.service;

/**
 * 无头浏览器PDF生成服务接口
 */
public interface HeadlessPdfExportService {
    
    /**
     * 将HTML内容转换为PDF（使用无头浏览器）
     * @param htmlContent HTML内容
     * @return PDF文件的字节数组
     */
    byte[] convertHtmlContentToPdf(String htmlContent) throws Exception;
}