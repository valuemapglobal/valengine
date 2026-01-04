package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rule_pool")
public class RulePool extends Model {

    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Integer id;

    private String strategy;

    private String ruleGroup;

    private String ruleCode;

    private String ruleDesc;

    /**
     * 分线级别
     * 1-低风险
     * 2-中低风险
     * 3-中风险
     * 4-中高风险
     * 5-高风险
     */
    private String riskLevel;

    private String hitAction;

    /**
     * 是否强拒绝
     * 1-是，0-否
     */
    private String stronglyReject;

    /**
     * 是否转人工
     * 1-是，0-否
     */
    private String transferToPerson;

    /**
     * 启用状态
     * 0-未启用
     * 1-启用
     */
    private String status;

    /**
     * 产品ID
     */
    private String referenceProduct;

    /**
     * 业务场景ID
     */
    private String businessScene;

    /**
     * 模型类型
     * 1-评分
     * 5-规则
     * 6-分类
     */
    private String modelType;

    private LocalDateTime createTime;
}
