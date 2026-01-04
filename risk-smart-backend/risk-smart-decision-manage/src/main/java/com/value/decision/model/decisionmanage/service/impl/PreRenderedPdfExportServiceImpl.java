package com.value.decision.model.decisionmanage.service.impl;

import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;
import com.value.decision.model.decisionmanage.service.HeadlessPdfExportService;
import com.value.decision.model.decisionmanage.service.PreRenderedPdfExportService;
import com.value.decision.model.decisionmanage.service.ReportTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预渲染PDF生成服务实现类
 */
@Service
@Slf4j
public class PreRenderedPdfExportServiceImpl implements PreRenderedPdfExportService {

    @Autowired
    private ReportTemplateService reportTemplateService;
    
    @Autowired
    private HeadlessPdfExportService headlessPdfExportService;

    @Override
    public byte[] generatePdfWithPreRendering(ModelTaskRecordVO modelTaskRecordVO) throws Exception {
        long startTime = System.currentTimeMillis();
        String taskId = "PRE_RENDER_PDF_" + System.currentTimeMillis();
        
        try {
            log.info("[{}] 开始预渲染PDF生成", taskId);
            
            // 1. 使用预渲染服务生成HTML内容
            log.info("[{}] 步骤1：生成预渲染HTML", taskId);
            long htmlStart = System.currentTimeMillis();
            String preRenderedHtml = reportTemplateService.generatePreRenderedHtml(modelTaskRecordVO);
            long htmlEnd = System.currentTimeMillis();
            
            log.info("[{}] 预渲染HTML生成完成，耗时: {} ms，HTML长度: {} 字符", 
                taskId, htmlEnd - htmlStart, preRenderedHtml.length());
            
            // 2. 使用简化的PDF生成服务
            log.info("[{}] 步骤2：生成PDF", taskId);
            long pdfStart = System.currentTimeMillis();
            byte[] pdfBytes = headlessPdfExportService.convertHtmlContentToPdf(preRenderedHtml);
            long pdfEnd = System.currentTimeMillis();
            
            log.info("[{}] PDF生成完成，耗时: {} ms，PDF大小: {} KB", 
                taskId, pdfEnd - pdfStart, pdfBytes.length / 1024);
            
            long totalTime = System.currentTimeMillis() - startTime;
            log.info("[{}] 预渲染PDF生成完成，总耗时: {} ms", taskId, totalTime);
            
            return pdfBytes;
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("[{}] 预渲染PDF生成失败，总耗时: {} ms", taskId, totalTime, e);
            throw new Exception("预渲染PDF生成失败：" + e.getMessage(), e);
        }
    }
}