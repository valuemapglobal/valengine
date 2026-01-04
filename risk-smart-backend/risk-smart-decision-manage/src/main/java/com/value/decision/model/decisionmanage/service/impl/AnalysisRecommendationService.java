package com.value.decision.model.decisionmanage.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.value.decision.model.decisionmanage.model.dto.EvaluationModuleDTO;
import com.value.decision.model.decisionmanage.model.dto.EvaluationReportDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelRuleResultDTO;
import com.value.decision.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分析建议生成服务
 * 基于模板化方法生成风险评估建议
 */
@Service
public class AnalysisRecommendationService {

    /**
     * 风险等级模板
     */
    private static final Map<String, RiskTemplate> RISK_TEMPLATES = new HashMap<>();

    static {
        // 低风险模板
        RISK_TEMPLATES.put("LOW", new RiskTemplate(
            "LOW",
            "低风险",
            "申请主体信用状况良好，各项指标均在正常范围内，风险可控。",
            "未发现明显风险信号，建议正常处理。",
            "建议维持现有授信政策，定期复查客户状况。",
            "按标准流程处理，无需额外风险控制措施。"
        ));

        // 中风险模板
        RISK_TEMPLATES.put("MEDIUM", new RiskTemplate(
            "MEDIUM",
            "中风险",
            "申请主体存在一定的风险因素，需要关注和监控。",
            "发现部分风险指标异常，建议加强风险管控。",
            "建议适当提高风险准备金，加强贷后管理。",
            "建议进行更详细的尽职调查，核实相关信息的真实性。"
        ));

        // 高风险模板
        RISK_TEMPLATES.put("HIGH", new RiskTemplate(
            "HIGH",
            "高风险",
            "申请主体存在显著风险因素，违约风险较高。",
            "发现多项高风险指标，建议谨慎处理。",
            "建议大幅提高风险准备金，采取严格的风险控制措施。",
            "建议进行全面的尽职调查，考虑要求提供担保或提高利率。"
        ));

        // 极高风险模板
        RISK_TEMPLATES.put("VERY_HIGH", new RiskTemplate(
            "VERY_HIGH",
            "极高风险",
            "申请主体存在严重风险因素，违约风险极高。",
            "发现严重风险信号，建议拒绝申请。",
            "建议拒绝本次申请，避免潜在的信贷损失。",
            "建议立即拒绝申请，如特殊情况需高级管理层审批。"
        ));
    }

    /**
     * 模型风险因素映射
     */
    private static final Map<Integer, List<String>> MODEL_RISK_FACTORS = new HashMap<>();

    static {
        // 评分模型风险因素
        MODEL_RISK_FACTORS.put(1, Arrays.asList(
            "信用评分偏低",
            "收入稳定性不足",
            "资产负债率过高",
            "债务负担过重"
        ));

        // 规则模型风险因素
        MODEL_RISK_FACTORS.put(5, Arrays.asList(
            "命中高风险规则",
            "存在强拒绝规则",
            "历史违约记录",
            "负面信息关联"
        ));

        // 分类模型风险因素
        MODEL_RISK_FACTORS.put(6, Arrays.asList(
            "司法案件记录",
            "税务异常",
            "经营异常",
            "负面舆情"
        ));
    }

    /**
     * 生成分析建议
     */
    public EvaluationReportDTO.AnalysisRecommendation generateRecommendation(
            String taskNo,
            List<EvaluationModuleDTO> modules,
            ModelRuleResultDTO modelResults,
            Map<String, Object> decisionInfo) {

        // 1. 分析风险等级
        String riskLevel = determineRiskLevel(modules, modelResults, decisionInfo);
        
        // 2. 获取对应模板
        RiskTemplate template = RISK_TEMPLATES.get(riskLevel);
        if (template == null) {
            template = RISK_TEMPLATES.get("MEDIUM"); // 默认中风险
        }

        // 3. 生成关键风险因素
        List<String> keyRiskFactors = generateKeyRiskFactors(modules, modelResults, riskLevel);

        // 4. 生成具体建议
        List<String> recommendations = generateSpecificRecommendations(modules, modelResults, riskLevel);

        // 5. 构建分析建议对象
        EvaluationReportDTO.AnalysisRecommendation recommendation = new EvaluationReportDTO.AnalysisRecommendation();
        recommendation.setOverallAssessment(template.getOverallAssessment());
        recommendation.setRiskWarning(template.getRiskWarning());
        recommendation.setSuggestion(template.getSuggestion());
        recommendation.setFollowUpActions(template.getFollowUpActions());
        recommendation.setRiskLevel(template.getRiskLevelName());
        recommendation.setKeyRiskFactors(keyRiskFactors);
        recommendation.setRecommendations(recommendations);
        recommendation.setGenerationTime(new Date());

        return recommendation;
    }

    /**
     * 确定风险等级
     */
    private String determineRiskLevel(List<EvaluationModuleDTO> modules, 
                                     ModelRuleResultDTO modelResults, 
                                     Map<String, Object> decisionInfo) {
        int riskScore = 0;

        // 分析评分模型
        if (modelResults.getScoreData() != null) {
            JSONObject scoreData = modelResults.getScoreData();
            if (scoreData.containsKey("finalScore")) {
                Double score = scoreData.getDouble("finalScore");
                if (score != null) {
                    if (score < 400) riskScore += 30;
                    else if (score < 600) riskScore += 20;
                    else if (score < 750) riskScore += 10;
                }
            }
        }

        // 分析规则模型
        if (modelResults.getRuleData() != null) {
            JSONObject ruleData = modelResults.getRuleData();
            if (ruleData.containsKey("hitRules")) {
                List<JSONObject> hitRules = ruleData.getJSONArray("hitRules").toJavaList(JSONObject.class);
                for (JSONObject rule : hitRules) {
                    String riskLevel = rule.getString("riskLevel");
                    String stronglyReject = rule.getString("stronglyReject");
                    
                    if ("HIGH".equals(riskLevel)) riskScore += 25;
                    else if ("MEDIUM".equals(riskLevel)) riskScore += 15;
                    else if ("LOW".equals(riskLevel)) riskScore += 5;
                    
                    if ("强拒".equals(stronglyReject)) riskScore += 20;
                }
            }
        }

        // 分析分类模型
        if (modelResults.getSortData() != null) {
            JSONObject sortData = modelResults.getSortData();
            if (sortData.containsKey("companyModules")) {
                JSONObject companyModules = sortData.getJSONObject("companyModules");
                if (companyModules.containsKey("judicialDerivationData")) {
                    List<JSONObject> judicialData = companyModules.getJSONArray("judicialDerivationData")
                        .toJavaList(JSONObject.class);
                    if (!judicialData.isEmpty()) {
                        riskScore += 15 + Math.min(judicialData.size() * 5, 25);
                    }
                }
            }
        }

        // 根据总分确定风险等级
        if (riskScore >= 80) return "VERY_HIGH";
        if (riskScore >= 60) return "HIGH";
        if (riskScore >= 30) return "MEDIUM";
        return "LOW";
    }

    /**
     * 生成关键风险因素
     */
    private List<String> generateKeyRiskFactors(List<EvaluationModuleDTO> modules, 
                                               ModelRuleResultDTO modelResults, 
                                               String riskLevel) {
        List<String> factors = new ArrayList<>();

        // 根据模型结果生成风险因素
        for (EvaluationModuleDTO module : modules) {
            // 根据模型类型映射到对应的moduleId
            Integer moduleId = null;
            if ("评分模型".equals(module.getModelType())) {
                moduleId = 1;
            } else if ("规则模型".equals(module.getModelType())) {
                moduleId = 5; // 注意：这里使用5而不是2，因为之前的代码用的是5
            } else if ("分类模型".equals(module.getModelType())) {
                moduleId = 6;
            }
            
            if (moduleId != null) {
                List<String> modelFactors = MODEL_RISK_FACTORS.get(moduleId);
                if (modelFactors != null) {
                    // 根据风险等级选择对应数量的风险因素
                    int factorCount = "LOW".equals(riskLevel) ? 1 : 
                                     "MEDIUM".equals(riskLevel) ? 2 : 
                                     "HIGH".equals(riskLevel) ? 3 : 4;
                    
                    factors.addAll(modelFactors.subList(0, Math.min(factorCount, modelFactors.size())));
                }
            }
        }

        return factors;
    }

    /**
     * 生成具体建议
     */
    private List<String> generateSpecificRecommendations(List<EvaluationModuleDTO> modules, 
                                                        ModelRuleResultDTO modelResults, 
                                                        String riskLevel) {
        List<String> recommendations = new ArrayList<>();

        // 基础建议
        recommendations.add("建议定期监控申请主体的信用状况变化");
        recommendations.add("建议建立风险预警机制，及时发现潜在风险");

        // 基于规则命中动作生成具体建议
        List<String> ruleBasedRecommendations = generateRuleBasedRecommendations(modules, modelResults);
        recommendations.addAll(ruleBasedRecommendations);

        // 根据风险等级添加特定建议
        switch (riskLevel) {
            case "LOW":
                recommendations.add("建议按照标准流程处理，无需额外措施");
                break;
            case "MEDIUM":
                recommendations.add("建议加强贷后管理，提高检查频率");
                recommendations.add("建议适当控制授信额度");
                break;
            case "HIGH":
                recommendations.add("建议要求提供额外担保措施");
                recommendations.add("建议提高风险准备金比例");
                recommendations.add("建议进行更详细的尽职调查");
                break;
            case "VERY_HIGH":
                recommendations.add("建议拒绝本次申请");
                recommendations.add("建议将申请主体列入观察名单");
                recommendations.add("建议进行全面的背景调查");
                break;
        }

        return recommendations;
    }

    /**
     * 基于规则命中动作生成建议
     */
    private List<String> generateRuleBasedRecommendations(List<EvaluationModuleDTO> modules, 
                                                         ModelRuleResultDTO modelResults) {
        List<String> recommendations = new ArrayList<>();

        // 处理规则模型的命中动作
        if (modelResults.getRuleData() != null) {
            JSONObject ruleData = modelResults.getRuleData();
            if (ruleData.containsKey("hitRules")) {
                List<JSONObject> hitRules = ruleData.getJSONArray("hitRules").toJavaList(JSONObject.class);
                
                for (JSONObject rule : hitRules) {
                    String hitAction = rule.getString("hitAction");
                    String riskLevel = rule.getString("riskLevel");
                    String stronglyReject = rule.getString("stronglyReject");
                    
                    // 基于命中动作生成建议
                    if (StringUtils.isNotEmpty(hitAction)) {
                        String recommendation = mapHitActionToRecommendation(hitAction, riskLevel, stronglyReject);
                        if (StringUtils.isNotEmpty(recommendation)) {
                            recommendations.add(recommendation);
                        }
                    }
                }
            }
        }

        // 处理评分模型的结果
        if (modelResults.getScoreData() != null) {
            JSONObject scoreData = modelResults.getScoreData();
            if (scoreData.containsKey("finalScore")) {
                Double score = scoreData.getDouble("finalScore");
                if (score != null && score < 600) {
                    recommendations.add("评分较低，建议关注申请人的还款能力和信用历史");
                }
            }
        }

        // 处理分类模型的结果
        if (modelResults.getSortData() != null) {
            JSONObject sortData = modelResults.getSortData();
            if (sortData.containsKey("companyModules")) {
                JSONObject companyModules = sortData.getJSONObject("companyModules");
                if (companyModules.containsKey("judicialDerivationData")) {
                    List<JSONObject> judicialData = companyModules.getJSONArray("judicialDerivationData")
                        .toJavaList(JSONObject.class);
                    if (!judicialData.isEmpty()) {
                        recommendations.add("存在司法案件记录，建议详细了解案件性质和影响");
                    }
                }
            }
        }

        return recommendations.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 将命中动作映射为具体建议
     */
    private String mapHitActionToRecommendation(String hitAction, String riskLevel, String stronglyReject) {
        if (StringUtils.isEmpty(hitAction)) {
            return null;
        }

        // 标准化命中动作文本
        String action = hitAction.toLowerCase();
        
        // 基于命中动作内容生成建议
        if (action.contains("拒绝") || action.contains("拒绝申请")) {
            return "建议拒绝本次申请，风险较高";
        } else if (action.contains("担保") || action.contains("抵押")) {
            return "建议要求提供担保或抵押措施";
        } else if (action.contains("调查") || action.contains("核实")) {
            return "建议进行详细的背景调查和信息核实";
        } else if (action.contains("监控") || action.contains("关注")) {
            return "建议加强贷后监控和风险预警";
        } else if (action.contains("额度") || action.contains("授信")) {
            return "建议适当控制授信额度和贷款期限";
        } else if (action.contains("人工") || action.contains("复核")) {
            return "建议转人工复核处理";
        } else if (action.contains("材料") || action.contains("证明")) {
            return "建议要求提供额外的证明材料";
        } else if ("强拒".equals(stronglyReject)) {
            return "强拒规则命中，建议直接拒绝申请";
        } else if ("HIGH".equals(riskLevel)) {
            return "高风险规则命中，建议谨慎处理";
        } else if ("MEDIUM".equals(riskLevel)) {
            return "中风险规则命中，建议适当关注";
        }

        // 默认建议
        return "根据命中规则建议：" + hitAction;
    }

    /**
     * 风险模板内部类
     */
    private static class RiskTemplate {
        private String code;
        private String riskLevelName;
        private String overallAssessment;
        private String riskWarning;
        private String suggestion;
        private String followUpActions;

        public RiskTemplate(String code, String riskLevelName, String overallAssessment, 
                          String riskWarning, String suggestion, String followUpActions) {
            this.code = code;
            this.riskLevelName = riskLevelName;
            this.overallAssessment = overallAssessment;
            this.riskWarning = riskWarning;
            this.suggestion = suggestion;
            this.followUpActions = followUpActions;
        }

        // Getters
        public String getCode() { return code; }
        public String getRiskLevelName() { return riskLevelName; }
        public String getOverallAssessment() { return overallAssessment; }
        public String getRiskWarning() { return riskWarning; }
        public String getSuggestion() { return suggestion; }
        public String getFollowUpActions() { return followUpActions; }
    }
}