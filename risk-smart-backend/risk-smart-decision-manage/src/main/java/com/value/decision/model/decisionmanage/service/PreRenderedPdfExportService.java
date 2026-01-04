package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;

/**
 * 预渲染PDF生成服务接口
 */
public interface PreRenderedPdfExportService {
    
    /**
     * 使用预渲染方式生成PDF
     * @param modelTaskRecordVO 任务记录查询参数
     * @return PDF文件的字节数组
     */
    byte[] generatePdfWithPreRendering(ModelTaskRecordVO modelTaskRecordVO) throws Exception;
}