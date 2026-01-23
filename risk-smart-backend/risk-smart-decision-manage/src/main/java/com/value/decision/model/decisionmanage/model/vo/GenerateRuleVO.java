package com.value.decision.model.decisionmanage.model.vo;

import com.value.decision.model.rdenew.vo.RdeRiskVariableArrayVO;
import lombok.Data;

import java.util.List;

@Data
public class GenerateRuleVO {

    //条件集合
    private List<RdeRiskVariableArrayVO> conditionArray;

    //code
    private String code;

    //策略导航标识  1,评分，2评级，3额度，4定价，5规则 6分类
    private String ruleCode;

    private String businessCode;

    //数据标识  数据类型 1元数据 2特征变量 3分析指标
    private String dataType;

    //分析指标属性名
    private String objStats;

    //属性结果
    private String objResult;

    //属性结果
    private String objResultType;

    //是否属于 集合size判断  0为否 1为是
    private Integer arraySize;

    //规则优先级  值越高的规则，优先级越高，越先执行
    private Integer salience;

    //预警数据对比属性
    private List<WarnComparisonVO> objResultCompare;

}
