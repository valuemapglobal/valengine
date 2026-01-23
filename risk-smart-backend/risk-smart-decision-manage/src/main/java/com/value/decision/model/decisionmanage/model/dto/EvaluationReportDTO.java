package com.value.decision.model.decisionmanage.model.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 评估报告的顶层数据传输对象
 */
@Data
public class EvaluationReportDTO {

    // 原有字段
    private String processName;
    private String taskNo;
    private Date executionTime;
    private List<EvaluationModuleDTO> modules;

    // 新增任务基本信息字段
    private String applicationUser;      // 申请用户
    private String applicationUserId;    // 申请用户ID
    private String deptName;            // 所属部门名称
    private Integer deptId;             // 所属部门ID
    private String businessScenario;    // 业务场景描述
    private String businessCode;        // 业务场景代码
    private String productName;         // 产品名称
    private Integer productId;          // 产品ID
    private String processStrategy;     // 流程策略名称
    private Integer processId;          // 流程策略ID
    private String taskStatusDesc;      // 任务状态描述
    private Integer taskStatus;         // 任务状态码
    private String responseFormDesc;    // 响应形式描述
    private Integer responseForm;       // 响应形式码
    
    // 流程策略详细信息
    private ProcessStrategyInfo processStrategyInfo;
    
    // 产品详细信息
    private ProductInfo productInfo;
    
    // 决策结果信息
    private DecisionInfo decisionInfo;
    
    // 分析建议信息
    private AnalysisRecommendation analysisRecommendation;
    
    // 总评分计算信息
    private TotalScoreInfo totalScoreInfo;

    /**
     * 流程策略信息
     */
    @Data
    public static class ProcessStrategyInfo {
        private String strategyName;        // 策略名称
        private String businessScenario;    // 业务场景
        private String productName;         // 产品名称
        private String useStatus;          // 启用状态
        private Date createTime;           // 创建时间
        private Date updateTime;           // 最后更新时间
        private String versionControl;     // 版本控制
        private String description;        // 策略描述
    }

    /**
     * 产品信息
     */
    @Data
    public static class ProductInfo {
        private String productName;        // 产品名称
        private String productId;          // 产品ID
        private String deptName;           // 所属部门
        private String productType;        // 产品类型
        private Date createTime;           // 创建时间
        private Date updateTime;           // 最后更新时间
        private String dataStatus;         // 数据状态
    }

    /**
     * 决策结果信息
     */
    @Data
    public static class DecisionInfo {
        private String finalDecisionCode;      // 最终决策代码
        private String decisionLevel;          // 决策等级
        private String riskType;              // 风险类型
        private String needManualReview;      // 是否转人工
        private String decisionDescription;   // 决策说明
        private Map<String, List<String>> decisionConditions; // 按风险等级分组的决策条件
        private Date startTime;               // 开始时间
        private Date endTime;                 // 结束时间
        private String finalStatus;          // 最终状态
        private String reviewStatus;         // 复核状态
        private Long totalDuration;          // 总耗时
        private Integer modelCount;          // 模型数
        private Integer riskScore;           // 风险评分
    }

    /**
     * 分析建议信息
     */
    @Data
    public static class AnalysisRecommendation {
        private String overallAssessment;     // 总体评估
        private String riskWarning;           // 风险预警
        private String suggestion;            // 建议措施
        private String followUpActions;       // 后续行动
        private String riskLevel;             // 风险等级
        private List<String> keyRiskFactors;  // 关键风险因素
        private List<String> recommendations; // 具体建议列表
        private Date generationTime;          // 生成时间
    }

    /**
     * 总评分计算信息
     */
    @Data
    public static class TotalScoreInfo {
        private Integer initialScore;          // 初始分数 (950)
        private Integer finalScore;            // 最终得分
        private Integer totalDeduction;        // 总扣分
        private Double scoreModelDeduction;    // 评分模型扣分
        private Double ruleModelDeduction;     // 规则模型扣分
        private Double categoryModelDeduction; // 分类模型扣分
        private Integer scoreModelRiskPoints;  // 评分模型风险点
        private Integer ruleModelRiskPoints;   // 规则模型风险点
        private Integer categoryModelRiskPoints; // 分类模型风险点
        private String scoreLevel;             // 评分等级
        private Boolean needManualReview;      // 是否需要人工复核
    }
}