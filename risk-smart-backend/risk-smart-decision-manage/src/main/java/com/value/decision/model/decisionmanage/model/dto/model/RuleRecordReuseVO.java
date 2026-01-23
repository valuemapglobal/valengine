package com.value.decision.model.decisionmanage.model.dto.model;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import lombok.Data;

import java.util.List;

@Data
public class RuleRecordReuseVO {

    /**
     * 自建产品id
     */
    private Integer buildProjectCode;

    /**
     * 自建业务场景id
     */
    private Integer buildBusinessCode;

    /**
     * 自建策略导航id
     */
    private Integer buildRuleCode;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 标准策略/规则组/规则id
     */
    private Integer parentCardId;

    /**
     * 自建策略id
     */
    private Integer ruleId;

    /**
     * 自建规则组id
     */
    private Integer groupId;

    /**
     * 模块区分 1策略2规则组3规则
     */
    private Integer moudleId;

    /**
     * 按钮状态  1启用0停用
     */
    private Boolean buttonState;

    /**
     * 版本控制
     */
    private String versionControl;

    /**
     * 引用的标准策略数据
     */
    private List<RdeModelAntiFraud> ruleRecordData;

    /**
     * 引用的标准规则组数据
     */
    private List<RdeModelAntiFraudRuleGroup> ruleRecordRuleGroupData;

    /**
     * 引用的标准规则数据
     */
    private List<RdeModelAntiFraudRuleRecord> ruleRecordRuleRecordData;

}
