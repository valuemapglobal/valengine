package com.value.decision.model.decisionmanage.service.impl;

import com.value.decision.model.decisionmanage.model.dto.EvaluationModuleDTO;
import com.value.decision.model.decisionmanage.model.dto.EvaluationReportDTO;
import com.value.decision.model.decisionmanage.model.dto.EvaluationRuleDetailDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelRuleResultDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 总评分计算服务
 * 基于"独立风险池"模型计算最终风险评分
 */
@Service
public class TotalScoreCalculationService {

    // 模型权重配置
    private static final double SCORE_MODEL_WEIGHT = 0.33;    // 评分模型权重
    private static final double RULE_MODEL_WEIGHT = 0.34;     // 规则模型权重
    private static final double CATEGORY_MODEL_WEIGHT = 0.33; // 分类模型权重

    // 基础分数配置
    private static final int INITIAL_SCORE = 950;              // 初始分数
    private static final int MIN_SCORE = 350;                 // 最低分数
    private static final int MAX_SCORE = 950;                 // 最高分数

    // 风险点上限配置
    private static final int RULE_MODEL_MAX_POINTS = 250;     // 规则模型风险点上限
    private static final int CATEGORY_MODEL_MAX_POINTS = 150; // 分类模型风险点上限

    /**
     * 计算总评分
     */
    public EvaluationReportDTO.TotalScoreInfo calculateTotalScore(
            List<EvaluationModuleDTO> modules,
            ModelRuleResultDTO modelResults) {

        // 1. 计算各模型的独立风险池
        RiskPoolResult riskPoolResult = calculateRiskPools(modules, modelResults);

        // 2. 加权计算总扣分
        double totalDeduction = calculateWeightedDeduction(riskPoolResult);

        // 3. 计算最终得分
        int finalScore = calculateFinalScore(totalDeduction);

        // 4. 判断是否需要人工复核
        boolean needManualReview = determineManualReview(finalScore);

        // 5. 确定评分等级
        String scoreLevel = determineScoreLevel(finalScore);

        // 6. 构建结果对象
        EvaluationReportDTO.TotalScoreInfo totalScoreInfo = new EvaluationReportDTO.TotalScoreInfo();
        totalScoreInfo.setInitialScore(INITIAL_SCORE);
        totalScoreInfo.setFinalScore(finalScore);
        totalScoreInfo.setTotalDeduction((int) Math.round(totalDeduction));
        
        // 精确到小数点后一位
        BigDecimal scoreModelDeduction = BigDecimal.valueOf(riskPoolResult.getScorePoolPoints() * SCORE_MODEL_WEIGHT)
            .setScale(1, RoundingMode.HALF_UP);
        BigDecimal ruleModelDeduction = BigDecimal.valueOf(riskPoolResult.getRulePoolPoints() * RULE_MODEL_WEIGHT)
            .setScale(1, RoundingMode.HALF_UP);
        BigDecimal categoryModelDeduction = BigDecimal.valueOf(riskPoolResult.getCategoryPoolPoints() * CATEGORY_MODEL_WEIGHT)
            .setScale(1, RoundingMode.HALF_UP);
        
        totalScoreInfo.setScoreModelDeduction(scoreModelDeduction.doubleValue());
        totalScoreInfo.setRuleModelDeduction(ruleModelDeduction.doubleValue());
        totalScoreInfo.setCategoryModelDeduction(categoryModelDeduction.doubleValue());
        totalScoreInfo.setScoreModelRiskPoints(riskPoolResult.getScorePoolPoints());
        totalScoreInfo.setRuleModelRiskPoints(riskPoolResult.getRulePoolPoints());
        totalScoreInfo.setCategoryModelRiskPoints(riskPoolResult.getCategoryPoolPoints());
        totalScoreInfo.setScoreLevel(scoreLevel);
        totalScoreInfo.setNeedManualReview(needManualReview);

        return totalScoreInfo;
    }

    /**
     * 计算各模型的独立风险池
     */
    private RiskPoolResult calculateRiskPools(List<EvaluationModuleDTO> modules, ModelRuleResultDTO modelResults) {
        int scorePoolPoints = 0;
        int rulePoolPoints = 0;
        int categoryPoolPoints = 0;

        for (EvaluationModuleDTO module : modules) {
            if ("评分模型".equals(module.getModelType())) {
                // A. 评分模型风险池: (950 - 评分模型得分)
                if (module.getFinalScore() != null) {
                    scorePoolPoints = INITIAL_SCORE - module.getFinalScore().intValue();
                }
            } else if ("规则模型".equals(module.getModelType())) {
                // B. 规则模型风险池: 根据命中规则的风险等级累积风险点
                rulePoolPoints += calculateRuleModelRiskPoints(module);
            } else if ("分类模型".equals(module.getModelType())) {
                // C. 分类模型风险池: 根据负面记录数量累积风险点
                categoryPoolPoints = calculateCategoryModelRiskPoints(module);
            }
        }

        // 应用风险点上限
        rulePoolPoints = Math.min(rulePoolPoints, RULE_MODEL_MAX_POINTS);
        categoryPoolPoints = Math.min(categoryPoolPoints, CATEGORY_MODEL_MAX_POINTS);

        return new RiskPoolResult(scorePoolPoints, rulePoolPoints, categoryPoolPoints);
    }

    /**
     * 计算规则模型风险点
     */
    private int calculateRuleModelRiskPoints(EvaluationModuleDTO module) {
        int riskPoints = 0;
        
        if (module.getDetails() != null) {
            for (EvaluationRuleDetailDTO detail : module.getDetails()) {
                String riskLevel = detail.getRiskLevel();
                if (riskLevel != null) {
                    switch (riskLevel) {
                        case "1": // 低风险（通过）- 风险点最低
                            riskPoints += 10;
                            break;
                        case "3": // 中风险（预警）- 风险点中等
                            riskPoints += 30;
                            break;
                        case "5": // 高风险（拒绝）- 风险点最高
                            riskPoints += 60;
                            break;
                    }
                }
            }
        }
        
        return riskPoints;
    }

    /**
     * 计算分类模型风险点
     */
    private int calculateCategoryModelRiskPoints(EvaluationModuleDTO module) {
        int recordCount = 0;

        if (module.getDetails() != null) {
            recordCount = module.getDetails().stream()
                    .filter(detail -> "ClassificationResult".equals(detail.getRuleCode()) && detail.getDynamicData() != null)
                    .mapToInt(detail -> detail.getDynamicData().size())
                    .sum();
        }

        // 根据记录数量确定风险点
        if (recordCount >= 11) {
            return 150; // 11+ 条记录
        } else if (recordCount >= 6) {
            return 80;  // 6-10 条记录
        } else if (recordCount >= 1) {
            return 40;  // 1-5 条记录
        }
        
        return 0; // 无记录
    }

    /**
     * 加权计算总扣分
     */
    private double calculateWeightedDeduction(RiskPoolResult riskPoolResult) {
        return (riskPoolResult.getScorePoolPoints() * SCORE_MODEL_WEIGHT) +
               (riskPoolResult.getRulePoolPoints() * RULE_MODEL_WEIGHT) +
               (riskPoolResult.getCategoryPoolPoints() * CATEGORY_MODEL_WEIGHT);
    }

    /**
     * 计算最终得分
     */
    private int calculateFinalScore(double totalDeduction) {
        int finalScore = INITIAL_SCORE - (int) Math.round(totalDeduction);
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, finalScore));
    }

    /**
     * 判断是否需要人工复核 - 基于风险等级的转人工策略
     */
    private boolean determineManualReview(int finalScore) {
        // 基于风险等级的人工复核判断逻辑
        if (finalScore < 600) {
            return true; // 高风险(350-599分): 强制转人工
        } else if (finalScore < 750) {
            return true; // 中风险(600-749分): 建议转人工
        } else {
            return false; // 低风险(750-950分): 自动通过
        }
    }

    /**
     * 人工复核判断服务 - 基于风险等级的转人工策略
     * 提供对外接口进行人工复核决策
     */
    public boolean shouldRequireManualReview(int finalScore, String scoreLevel) {
        // 基于新的风险等级策略进行判断
        return determineManualReview(finalScore);
    }

    /**
     * 获取人工复核建议 - 基于风险等级的转人工策略
     */
    public String getManualReviewSuggestion(int finalScore, String scoreLevel) {
        if (finalScore < 600) {
            return "高风险：分数已表明存在显著风险，必须由人工进行最终审核";
        } else if (finalScore < 750) {
            return "中风险：系统可以自动通过，但建议人工复核，业务人员可根据判断决定是否要复核";
        } else {
            return "低风险：风险较低，建议自动通过，无需人工干预";
        }
    }

    /**
     * 确定评分等级 - 基于新的风险等级范围
     */
    private String determineScoreLevel(int finalScore) {
        if (finalScore >= 850) {
            return "AAA";  // 低风险-优秀
        } else if (finalScore >= 750) {
            return "AA";   // 低风险-良好
        } else if (finalScore >= 600) {
            return "A";    // 中风险-一般
        } else {
            return "B";    // 高风险-较差
        }
    }

    /**
     * 获取风险等级描述
     */
    public String getRiskLevelDescription(int finalScore) {
        if (finalScore < 600) {
            return "高风险";
        } else if (finalScore < 750) {
            return "中风险";
        } else {
            return "低风险";
        }
    }

    /**
     * 获取转人工策略描述
     */
    public String getManualReviewStrategy(int finalScore) {
        if (finalScore < 600) {
            return "强制转人工";
        } else if (finalScore < 750) {
            return "建议转人工";
        } else {
            return "自动通过";
        }
    }

    /**
     * 风险池结果内部类
     */
    private static class RiskPoolResult {
        private int scorePoolPoints;      // 评分模型风险点
        private int rulePoolPoints;       // 规则模型风险点
        private int categoryPoolPoints;   // 分类模型风险点

        public RiskPoolResult(int scorePoolPoints, int rulePoolPoints, int categoryPoolPoints) {
            this.scorePoolPoints = scorePoolPoints;
            this.rulePoolPoints = rulePoolPoints;
            this.categoryPoolPoints = categoryPoolPoints;
        }

        // Getters
        public int getScorePoolPoints() { return scorePoolPoints; }
        public int getRulePoolPoints() { return rulePoolPoints; }
        public int getCategoryPoolPoints() { return categoryPoolPoints; }
    }
}