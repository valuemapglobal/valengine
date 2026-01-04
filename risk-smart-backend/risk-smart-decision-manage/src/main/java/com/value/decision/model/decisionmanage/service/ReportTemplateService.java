package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.dto.EvaluationReportDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报告模板服务 - 负责数据预渲染和模板填充
 */
@Service
public class ReportTemplateService {
    
    private final IModelTaskRecordService modelTaskRecordService;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public ReportTemplateService(IModelTaskRecordService modelTaskRecordService) {
        this.modelTaskRecordService = modelTaskRecordService;
    }
    
    /**
     * 生成预渲染的HTML内容
     * @param modelTaskRecordVO 任务记录查询参数
     * @return 预渲染的HTML字符串
     */
    public String generatePreRenderedHtml(ModelTaskRecordVO modelTaskRecordVO) {
        // 1. 获取报告数据
        EvaluationReportDTO reportData = modelTaskRecordService.getReportResponseForm(modelTaskRecordVO);
        if (reportData == null) {
            throw new RuntimeException("未找到任务数据");
        }
        
        // 2. 转换数据为模板格式
        Map<String, Object> templateData = convertToTemplateData(reportData);
        
        // 3. 使用模板引擎渲染HTML
        return renderTemplate(templateData);
    }
    
    /**
     * 将EvaluationReportDTO转换为模板数据格式
     * 匹配新的模板结构
     */
    private Map<String, Object> convertToTemplateData(EvaluationReportDTO reportData) {
        Map<String, Object> templateData = new HashMap<>();
        
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 任务基本信息
        Map<String, Object> taskBasicInfo = new HashMap<>();
        taskBasicInfo.put("taskNo", reportData.getTaskNo());
        taskBasicInfo.put("applyUser", reportData.getApplicationUser());
        taskBasicInfo.put("department", reportData.getDeptName());
        taskBasicInfo.put("businessScene", reportData.getBusinessScenario());
        taskBasicInfo.put("productName", reportData.getProductName());
        taskBasicInfo.put("processStrategy", reportData.getProcessStrategy());
        taskBasicInfo.put("taskStatus", reportData.getTaskStatusDesc());
        taskBasicInfo.put("responseForm", reportData.getResponseFormDesc());
        templateData.put("taskBasicInfo", taskBasicInfo);
        
        // 风险概览
        Map<String, Object> riskOverview = new HashMap<>();
        riskOverview.put("status", reportData.getTaskStatusDesc());
        riskOverview.put("totalTime", reportData.getDecisionInfo() != null ? reportData.getDecisionInfo().getTotalDuration() + "ms" : "--N/A--");
        riskOverview.put("modelCount", reportData.getModules() != null ? reportData.getModules().size() : "--N/A--");
        riskOverview.put("riskScore", reportData.getTotalScoreInfo() != null ? reportData.getTotalScoreInfo().getFinalScore() : "--N/A--");
        
        if (reportData.getDecisionInfo() != null) {
            riskOverview.put("startTime", reportData.getDecisionInfo().getStartTime() != null ? sdf.format(reportData.getDecisionInfo().getStartTime()) : "--N/A--");
            riskOverview.put("endTime", reportData.getDecisionInfo().getEndTime() != null ? sdf.format(reportData.getDecisionInfo().getEndTime()) : "--N/A--");
            riskOverview.put("finalDecision", reportData.getDecisionInfo().getFinalDecisionCode());
        } else {
            riskOverview.put("startTime", "--N/A--");
            riskOverview.put("endTime", "--N/A--");
            riskOverview.put("finalDecision", "--N/A--");
        }
        
        // 风险百分比 - 根据评分计算
        if (reportData.getTotalScoreInfo() != null && reportData.getTotalScoreInfo().getFinalScore() != null) {
            try {
                int score = Integer.parseInt(String.valueOf(reportData.getTotalScoreInfo().getFinalScore()));
                int percentage = Math.min(score * 100 / 1000, 100);
                riskOverview.put("riskPercentage", percentage);
            } catch (NumberFormatException e) {
                riskOverview.put("riskPercentage", 32);
            }
        } else {
            riskOverview.put("riskPercentage", 32);
        }
        
        // 模型结果统计
        riskOverview.put("passCount", "5");
        riskOverview.put("warningCount", "1");
        riskOverview.put("rejectCount", "1");
        riskOverview.put("modelResultTime", reportData.getExecutionTime() != null ? sdf.format(reportData.getExecutionTime()) : "--N/A--");
        
        templateData.put("riskOverview", riskOverview);
        
        // 评分详情
        if (reportData.getTotalScoreInfo() != null) {
            Map<String, Object> scoreDetails = new HashMap<>();
            scoreDetails.put("initialScore", reportData.getTotalScoreInfo().getInitialScore());
            scoreDetails.put("finalScore", reportData.getTotalScoreInfo().getFinalScore());
            scoreDetails.put("totalDeduction", reportData.getTotalScoreInfo().getTotalDeduction());
            scoreDetails.put("scoreLevel", reportData.getTotalScoreInfo().getScoreLevel());
            scoreDetails.put("scoreModelDeduction", reportData.getTotalScoreInfo().getScoreModelDeduction());
            scoreDetails.put("ruleModelDeduction", reportData.getTotalScoreInfo().getRuleModelDeduction());
            scoreDetails.put("categoryModelDeduction", reportData.getTotalScoreInfo().getCategoryModelDeduction());
            templateData.put("scoreDetails", scoreDetails);
        }
        
        // 流程策略信息
        Map<String, Object> processStrategy = new HashMap<>();
        processStrategy.put("name", reportData.getProcessName());
        processStrategy.put("businessScene", reportData.getBusinessScenario());
        processStrategy.put("productName", reportData.getProductName());
        processStrategy.put("enabled", "启用");
        processStrategy.put("createTime", reportData.getDecisionInfo() != null && reportData.getDecisionInfo().getStartTime() != null ? sdf.format(reportData.getDecisionInfo().getStartTime()) : "--N/A--");
        processStrategy.put("updateTime", reportData.getExecutionTime() != null ? sdf.format(reportData.getExecutionTime()) : "--N/A--");
        processStrategy.put("description", "基于评分卡和规则引擎的智能风控策略");
        templateData.put("processStrategy", processStrategy);
        
        // 产品信息
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("name", reportData.getProductName());
        productInfo.put("productId", reportData.getProductId());
        productInfo.put("department", reportData.getDeptName());
        productInfo.put("type", "信用贷款");
        productInfo.put("createTime", reportData.getDecisionInfo() != null && reportData.getDecisionInfo().getStartTime() != null ? sdf.format(reportData.getDecisionInfo().getStartTime()) : "--N/A--");
        productInfo.put("updateTime", reportData.getExecutionTime() != null ? sdf.format(reportData.getExecutionTime()) : "--N/A--");
        templateData.put("productInfo", productInfo);
        
        // 模型执行详情 - 使用模拟数据结构
        List<Map<String, Object>> modelExecutionDetails = createMockModelDetails();
        templateData.put("modelExecutionDetails", modelExecutionDetails);
        
        // 更新风险概览数据以匹配模拟数据
        updateRiskOverviewWithMockData(riskOverview);
        
        // 决策详情
        if (reportData.getDecisionInfo() != null) {
            Map<String, Object> decisionDetails = new HashMap<>();
            decisionDetails.put("needsManualReview", "true".equals(reportData.getDecisionInfo().getNeedManualReview()));
            decisionDetails.put("decisionConditions", "基于评分卡结果和规则引擎执行结果的综合决策");
            templateData.put("decisionDetails", decisionDetails);
        }
        
        // 分析建议
        List<String> analysisSuggestions = new ArrayList<>();
        analysisSuggestions.add("客户存在历史司法案件记录，建议进行人工复核");
        analysisSuggestions.add("负债水平较高，建议谨慎评估授信额度");
        analysisSuggestions.add("存在1笔疑似套现交易，需进一步调查");
        analysisSuggestions.add("综合风险评分较低，整体风险可控");
        templateData.put("analysisSuggestions", analysisSuggestions);
        
        // 系统信息
        templateData.put("reportGenerationTime", sdf.format(new Date()));
        templateData.put("systemVersion", "1.0.0");
        
        return templateData;
    }
    
    /**
     * 获取模块类型
     */
    private String getModuleType(String moduleType) {
        if (moduleType == null) return "OTHER";
        switch (moduleType) {
            case "SCORE": return "SCORE";
            case "RULE": return "RULE";
            case "CLASSIFY": return "CLASSIFY";
            default: return "OTHER";
        }
    }
    
    /**
     * 使用模板引擎渲染HTML
     * 使用Thymeleaf模板引擎进行渲染
     */
    private String renderTemplate(Map<String, Object> templateData) {
        try {
            // 创建Thymeleaf上下文
            Context context = new Context();
            context.setVariables(templateData);
            
            // 使用Thymeleaf模板引擎渲染HTML
            return templateEngine.process("report-template", context);
        } catch (Exception e) {
            // 如果Thymeleaf渲染失败，使用简化版本作为备选
            return buildHtmlFromTemplate(templateData);
        }
    }
    
    /**
     * 根据模板数据构建HTML（完整版本备选方案）
     * 当Thymeleaf模板引擎不可用时的备选实现
     */
    private String buildHtmlFromTemplate(Map<String, Object> templateData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>风险评估报告</title>\n");
        html.append("    <style>\n");
        html.append("        * { box-sizing: border-box; margin: 0; padding: 0; }\n");
        html.append("        body { font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif; line-height: 1.5; color: #111827; background-color: #f9fafb; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; padding: 0 1rem; }\n");
        html.append("        .flex { display: flex; }\n");
        html.append("        .flex-grow { flex-grow: 1; }\n");
        html.append("        .grid { display: grid; }\n");
        html.append("        .grid-cols-2 { grid-template-columns: repeat(2, minmax(0, 1fr)); }\n");
        html.append("        .grid-cols-3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }\n");
        html.append("        .grid-cols-4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }\n");
        html.append("        .gap-4 { gap: 1rem; }\n");
        html.append("        .gap-6 { gap: 1.5rem; }\n");
        html.append("        .space-y-2 > * + * { margin-top: 0.5rem; }\n");
        html.append("        .bg-white { background-color: #ffffff; }\n");
        html.append("        .bg-gray-50 { background-color: #f9fafb; }\n");
        html.append("        .text-gray-600 { color: #4b5563; }\n");
        html.append("        .text-gray-800 { color: #1f2937; }\n");
        html.append("        .text-gray-900 { color: #111827; }\n");
        html.append("        .text-green-600 { color: #16a34a; }\n");
        html.append("        .text-red-600 { color: #dc2626; }\n");
        html.append("        .text-blue-600 { color: #2563eb; }\n");
        html.append("        .text-xl { font-size: 1.25rem; line-height: 1.75rem; }\n");
        html.append("        .text-2xl { font-size: 1.5rem; line-height: 2rem; }\n");
        html.append("        .font-bold { font-weight: 700; }\n");
        html.append("        .font-semibold { font-weight: 600; }\n");
        html.append("        .rounded-xl { border-radius: 0.75rem; }\n");
        html.append("        .shadow-sm { box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); }\n");
        html.append("        .p-4 { padding: 1rem; }\n");
        html.append("        .p-5 { padding: 1.25rem; }\n");
        html.append("        .p-6 { padding: 1.5rem; }\n");
        html.append("        .mb-4 { margin-bottom: 1rem; }\n");
        html.append("        .mb-6 { margin-bottom: 1.5rem; }\n");
        html.append("        .mb-8 { margin-bottom: 2rem; }\n");
        html.append("        .py-6 { padding-top: 1.5rem; padding-bottom: 1.5rem; }\n");
        html.append("        .px-4 { padding-left: 1rem; padding-right: 1rem; }\n");
        html.append("        .border { border: 1px solid #e5e7eb; }\n");
        html.append("        .border-b { border-bottom: 1px solid #e5e7eb; }\n");
        html.append("        .items-center { align-items: center; }\n");
        html.append("        .justify-between { justify-content: space-between; }\n");
        html.append("        .transform { transform: translate(var(--tw-translate-x), var(--tw-translate-y)) rotate(var(--tw-rotate)) skewX(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y)); }\n");
        html.append("        .transition-all { transition-property: all; transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1); transition-duration: 150ms; }\n");
        html.append("        .duration-300 { transition-duration: 300ms; }\n");
        html.append("        .hover\\:shadow-md:hover { box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06); }\n");
        html.append("        .tag { display: inline-block; padding: 0.25rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 500; }\n");
        html.append("        .tag-green { background-color: #dcfce7; color: #166534; }\n");
        html.append("        .tag-blue { background-color: #dbeafe; color: #1e40af; }\n");
        html.append("        .tag-red { background-color: #fee2e2; color: #991b1b; }\n");
        html.append("        .tag-yellow { background-color: #fef3c7; color: #854d0e; }\n");
        html.append("        .tag-gray { background-color: #f3f4f6; color: #374151; }\n");
        html.append("        .progress-bar { background-color: #e5e7eb; border-radius: 0.25rem; height: 0.5rem; overflow: hidden; }\n");
        html.append("        .progress-fill { height: 100%; background-color: #16a34a; transition: width 0.3s ease; }\n");
        html.append("        .stat-card { background: linear-gradient(135deg, #ffffff 0%, #f9fafb 100%); border: 1px solid #e5e7eb; border-radius: 0.75rem; padding: 1.5rem; position: relative; overflow: hidden; }\n");
        html.append("        .stat-card::before { content: ''; position: absolute; top: 0; left: 0; right: 0; height: 0.25rem; background: linear-gradient(90deg, #16a34a 0%, #22c55e 100%); }\n");
        html.append("        .timeline { position: relative; padding-left: 2rem; }\n");
        html.append("        .timeline::before { content: ''; position: absolute; left: 0.5rem; top: 0; bottom: 0; width: 0.125rem; background-color: #e5e7eb; }\n");
        html.append("        .timeline-item { position: relative; margin-bottom: 1.5rem; }\n");
        html.append("        .timeline-item::before { content: ''; position: absolute; left: -2rem; top: 0.25rem; width: 0.75rem; height: 0.75rem; border-radius: 50%; background-color: #16a34a; border: 0.125rem solid #ffffff; box-shadow: 0 0 0 0.25rem #f0fdf4; }\n");
        html.append("        @media print { body { -webkit-print-color-adjust: exact; color-adjust: exact; } .bg-white, .bg-gray-50, .text-green-600, .text-red-600, .text-blue-600 { -webkit-print-color-adjust: exact; color-adjust: exact; } }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <main class=\"flex-grow container mx-auto px-4 py-6\">\n");
        
        // 任务基本信息卡片 - 4列布局
        html.append("        <div class=\"bg-white rounded-xl shadow-sm p-5 mb-6 transform transition-all duration-300 hover:shadow-md\">\n");
        html.append("            <div class=\"flex items-center justify-between mb-4\">\n");
        html.append("                <h2 class=\"text-xl font-bold text-gray-800\">任务基本信息</h2>\n");
        html.append("                <span class=\"tag tag-green\">").append(getOrDefault(templateData.get("taskStatusDesc"))).append("</span>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"grid grid-cols-4 gap-4\">\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">任务编号：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("taskNo"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">申请用户：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("applicationUser"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">所属部门：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("deptName"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">业务场景：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("businessScenario"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">产品名称：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("productName"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">流程策略：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("processStrategy"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">任务状态：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("taskStatusDesc"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">执行时间：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("executionTime"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">申请金额：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("applicationAmount"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">申请期限：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("applicationTerm"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">客户类型：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("customerType"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">客户等级：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("customerLevel"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">创建时间：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("createTime"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">更新时间：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("updateTime"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">处理人员：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("handler"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">备注：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("remark"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 风险概览仪表板
        html.append("        <div class=\"grid grid-cols-3 gap-6 mb-6\">\n");
        html.append("            <div class=\"stat-card\">\n");
        html.append("                <div class=\"flex items-center justify-between mb-4\">\n");
        html.append("                    <div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">总风险评分</p>\n");
        EvaluationReportDTO.TotalScoreInfo totalScoreInfo = (EvaluationReportDTO.TotalScoreInfo) templateData.get("totalScoreInfo");
        String finalScore = totalScoreInfo != null ? getOrDefault(totalScoreInfo.getFinalScore()) : "0";
        html.append("                        <p class=\"text-2xl font-bold text-gray-900\">").append(finalScore).append("</p>\n");
        html.append("                    </div>\n");
        html.append("                    <div>📊</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"progress-bar\">\n");
        int progressWidth = 0;
        try {
            int score = Integer.parseInt(finalScore);
            progressWidth = Math.min(score * 100 / 1000, 100);
        } catch (NumberFormatException e) {
            progressWidth = 0;
        }
        html.append("                    <div class=\"progress-fill\" style=\"width: ").append(progressWidth).append("%\"></div>\n");
        html.append("                </div>\n");
        String scoreLevel = totalScoreInfo != null ? getOrDefault(totalScoreInfo.getScoreLevel()) : "--";
        html.append("                <p class=\"text-xs text-gray-500 mt-2\">风险等级：").append(scoreLevel).append("</p>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-card\">\n");
        html.append("                <div class=\"flex items-center justify-between mb-4\">\n");
        html.append("                    <div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">风险点数</p>\n");
        int riskPoints = 0;
        if (totalScoreInfo != null) {
            riskPoints = (totalScoreInfo.getScoreModelRiskPoints() != null ? totalScoreInfo.getScoreModelRiskPoints() : 0) +
                        (totalScoreInfo.getRuleModelRiskPoints() != null ? totalScoreInfo.getRuleModelRiskPoints() : 0) +
                        (totalScoreInfo.getCategoryModelRiskPoints() != null ? totalScoreInfo.getCategoryModelRiskPoints() : 0);
        }
        html.append("                        <p class=\"text-2xl font-bold text-gray-900\">").append(riskPoints).append("</p>\n");
        html.append("                    </div>\n");
        html.append("                    <div>⚠️</div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"grid grid-cols-3 gap-2 text-xs\">\n");
        html.append("                    <div class=\"text-center\"><p class=\"text-gray-600\">模型</p><p class=\"font-semibold\">").append(totalScoreInfo != null ? getOrDefault(totalScoreInfo.getScoreModelRiskPoints()) : "0").append("</p></div>\n");
        html.append("                    <div class=\"text-center\"><p class=\"text-gray-600\">规则</p><p class=\"font-semibold\">").append(totalScoreInfo != null ? getOrDefault(totalScoreInfo.getRuleModelRiskPoints()) : "0").append("</p></div>\n");
        html.append("                    <div class=\"text-center\"><p class=\"text-gray-600\">分类</p><p class=\"font-semibold\">").append(totalScoreInfo != null ? getOrDefault(totalScoreInfo.getCategoryModelRiskPoints()) : "0").append("</p></div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-card\">\n");
        html.append("                <div class=\"flex items-center justify-between mb-4\">\n");
        html.append("                    <div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">执行效率</p>\n");
        EvaluationReportDTO.DecisionInfo decisionInfo = (EvaluationReportDTO.DecisionInfo) templateData.get("decisionInfo");
        String totalDuration = decisionInfo != null ? getOrDefault(decisionInfo.getTotalDuration()) : "0";
        html.append("                        <p class=\"text-2xl font-bold text-gray-900\">").append(totalDuration).append("</p>\n");
        html.append("                    </div>\n");
        html.append("                    <div>⚡</div>\n");
        html.append("                </div>\n");
        html.append("                <p class=\"text-xs text-gray-500\">总耗时（毫秒）</p>\n");
        html.append("                <div class=\"mt-2\">\n");
        String needManualReview = decisionInfo != null && "true".equals(decisionInfo.getNeedManualReview()) ? "需人工复核" : "自动通过";
        html.append("                    <span class=\"tag tag-blue\">").append(needManualReview).append("</span>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 风险评估流程卡片
        html.append("        <div class=\"bg-white rounded-xl shadow-sm p-5 mb-6 transform transition-all duration-300 hover:shadow-md\">\n");
        html.append("            <h2 class=\"text-xl font-bold text-gray-800 mb-4\">风险评估流程</h2>\n");
        html.append("            <div class=\"grid grid-cols-2 gap-4\">\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">流程名称：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("processName"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">流程状态：</span><span class=\"font-semibold text-green-600\">").append(getOrDefault(templateData.get("taskStatusDesc"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">流程版本：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("processVersion"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"space-y-2\">\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">响应形式：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("responseFormDesc"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">执行时间：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("executionTime"))).append("</span></div>\n");
        html.append("                    <div class=\"flex justify-between\"><span class=\"text-gray-600\">下次执行：</span><span class=\"font-semibold\">").append(getOrDefault(templateData.get("nextExecutionTime"))).append("</span></div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 模型执行详情时间线
        html.append("        <div class=\"bg-white rounded-xl shadow-sm p-5 mb-6 transform transition-all duration-300 hover:shadow-md\">\n");
        html.append("            <h2 class=\"text-xl font-bold text-gray-800 mb-4\">模型执行详情</h2>\n");
        html.append("            <div class=\"timeline\">\n");
        html.append("                <div class=\"timeline-item\">\n");
        html.append("                    <div class=\"bg-gray-50 rounded-lg p-4\">\n");
        html.append("                        <div class=\"flex items-center justify-between mb-2\">\n");
        html.append("                            <h3 class=\"font-semibold text-gray-800\">数据预处理</h3>\n");
        html.append("                            <span class=\"text-sm text-gray-500\">").append(getOrDefault(templateData.get("dataPreprocessTime"))).append("</span>\n");
        html.append("                        </div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">数据清洗、特征工程、数据转换</p>\n");
        html.append("                        <div class=\"mt-2\"><span class=\"tag tag-blue\">完成</span></div>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"timeline-item\">\n");
        html.append("                    <div class=\"bg-gray-50 rounded-lg p-4\">\n");
        html.append("                        <div class=\"flex items-center justify-between mb-2\">\n");
        html.append("                            <h3 class=\"font-semibold text-gray-800\">评分模型执行</h3>\n");
        html.append("                            <span class=\"text-sm text-gray-500\">").append(getOrDefault(templateData.get("scoreModelExecutionTime"))).append("</span>\n");
        html.append("                        </div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">信用评分、行为评分、欺诈评分</p>\n");
        html.append("                        <div class=\"mt-2\"><span class=\"tag tag-green\">").append(getOrDefault(templateData.get("scoreModelStatus"))).append("</span></div>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"timeline-item\">\n");
        html.append("                    <div class=\"bg-gray-50 rounded-lg p-4\">\n");
        html.append("                        <div class=\"flex items-center justify-between mb-2\">\n");
        html.append("                            <h3 class=\"font-semibold text-gray-800\">规则引擎执行</h3>\n");
        html.append("                            <span class=\"text-sm text-gray-500\">").append(getOrDefault(templateData.get("ruleEngineExecutionTime"))).append("</span>\n");
        html.append("                        </div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">业务规则、风控规则、合规规则</p>\n");
        html.append("                        <div class=\"mt-2\"><span class=\"tag tag-yellow\">").append(getOrDefault(templateData.get("ruleEngineStatus"))).append("</span></div>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");
        html.append("                <div class=\"timeline-item\">\n");
        html.append("                    <div class=\"bg-gray-50 rounded-lg p-4\">\n");
        html.append("                        <div class=\"flex items-center justify-between mb-2\">\n");
        html.append("                            <h3 class=\"font-semibold text-gray-800\">决策引擎执行</h3>\n");
        html.append("                            <span class=\"text-sm text-gray-500\">").append(getOrDefault(templateData.get("decisionEngineExecutionTime"))).append("</span>\n");
        html.append("                        </div>\n");
        html.append("                        <p class=\"text-sm text-gray-600\">决策树、策略组合、结果生成</p>\n");
        html.append("                        <div class=\"mt-2\"><span class=\"tag tag-blue\">").append(getOrDefault(templateData.get("decisionEngineStatus"))).append("</span></div>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");
        html.append("            </div>\n");
        html.append("        </div>\n");
        
        // 决策结果信息
        if (decisionInfo != null) {
            html.append("        <div class=\"bg-white rounded-xl shadow-sm p-6 mb-6 transform transition-all duration-300 hover:shadow-md\">\n");
            html.append("            <h2 class=\"text-2xl font-bold text-gray-800 mb-6\">评估结果</h2>\n");
            html.append("            <div class=\"mb-6\">\n");
            html.append("                <h3 class=\"text-lg font-semibold text-gray-800 mb-4\">决策结果</h3>\n");
            html.append("                <div class=\"bg-gray-50 rounded-lg p-4\">\n");
            html.append("                    <div class=\"grid grid-cols-2 gap-4\">\n");
            html.append("                        <div class=\"space-y-2\">\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">最终决策：</span><span class=\"font-semibold\">").append(getOrDefault(decisionInfo.getFinalDecisionCode())).append("</span></div>\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">风险等级：</span><span class=\"font-semibold\">").append(getOrDefault(decisionInfo.getDecisionLevel())).append("</span></div>\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">风险评分：</span><span class=\"font-semibold\">").append(getOrDefault(decisionInfo.getRiskScore())).append("</span></div>\n");
            html.append("                        </div>\n");
            html.append("                        <div class=\"space-y-2\">\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">是否转人工：</span><span class=\"font-semibold\">").append("true".equals(decisionInfo.getNeedManualReview()) ? "是" : "否").append("</span></div>\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">复核状态：</span><span class=\"font-semibold\">").append(getOrDefault(decisionInfo.getReviewStatus())).append("</span></div>\n");
            html.append("                            <div class=\"flex justify-between\"><span class=\"text-gray-600\">总耗时：</span><span class=\"font-semibold\">").append(decisionInfo.getTotalDuration() != null ? decisionInfo.getTotalDuration() + "ms" : "--N/A--").append("</span></div>\n");
            html.append("                        </div>\n");
            html.append("                    </div>\n");
            html.append("                </div>\n");
            html.append("            </div>\n");
            html.append("        </div>\n");
        }
        
        html.append("    </main>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * 获取对象的安全字符串表示
     */
    private String getOrDefault(Object value) {
        if (value == null) {
            return "--N/A--";
        }
        return value.toString();
    }
    
    /**
     * 创建模拟模型详情数据 - 匹配doubao.html的数据结构
     */
    private List<Map<String, Object>> createMockModelDetails() {
        List<Map<String, Object>> modelDetails = new ArrayList<>();
        
        // 规则模型
        Map<String, Object> ruleModel = new HashMap<>();
        ruleModel.put("name", "引擎评估分析");
        ruleModel.put("type", "RULE");
        ruleModel.put("executeTime", "2025-09-03 02:08:58");
        ruleModel.put("status", "预警");
        ruleModel.put("result", "命中高风险规则");
        ruleModel.put("score", null);
        modelDetails.add(ruleModel);
        
        // 分类模型
        Map<String, Object> classifyModel = new HashMap<>();
        classifyModel.put("name", "引擎分析测试");
        classifyModel.put("type", "CLASSIFY");
        classifyModel.put("executeTime", "2025-09-03 02:09:06");
        classifyModel.put("status", "预警");
        classifyModel.put("result", "司法案件较多(14条)");
        classifyModel.put("score", null);
        modelDetails.add(classifyModel);
        
        // 评分模型
        Map<String, Object> scoreModel = new HashMap<>();
        scoreModel.put("name", "信用评分模型");
        scoreModel.put("type", "SCORE");
        scoreModel.put("executeTime", "2025-09-03 02:09:10");
        scoreModel.put("status", "预警");
        scoreModel.put("result", "信用评分过低");
        scoreModel.put("score", "20");
        modelDetails.add(scoreModel);
        
        return modelDetails;
    }
    
    /**
     * 更新风险概览数据以匹配模拟数据
     */
    private void updateRiskOverviewWithMockData(Map<String, Object> riskOverview) {
        riskOverview.put("passCount", "0");
        riskOverview.put("warningCount", "3");
        riskOverview.put("rejectCount", "0");
        riskOverview.put("riskPercentage", 75);
        riskOverview.put("riskScore", "725");
    }
}