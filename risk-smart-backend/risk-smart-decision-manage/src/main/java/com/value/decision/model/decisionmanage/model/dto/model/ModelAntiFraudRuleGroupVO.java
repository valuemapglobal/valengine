package com.value.decision.model.decisionmanage.model.dto.model;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ModelAntiFraudRuleGroupVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 模型ID
     */
    private Integer modelId;

    /**
     * 当前自建策略ID
     */
    private Integer ruleId;

    /**
     * 策略模型标识
     */
    @NotBlank(message = "策略模型标识不能为空")
    private String ruleCode;

    /**
     * 左侧产品导航标识
     */
    @NotBlank(message = "左侧产品导航标识不能为空")
    private String projectCode;

    /**
     * 业务场景导航标识
     */
    @NotBlank(message = "业务场景导航标识不能为空")
    private String businessCode;

    /**
     * 版本控制
     */
    private String versionControl;

    private Integer pageNum;
    private Integer pageSize;


    /**
     * 用户选择新增的数据
     */
    private List<RdeModelAntiFraudRuleGroup> modelAntiGroupDataList;

    /**
     * 用户选择移除的数据
     */
    private List<RdeModelAntiFraudRuleGroup> modelAntiGroupDataDelList;

    /**
     * 用户选择新增的标准数据
     */
    private List<RdeModelAntiFraudRuleGroupSnapshot> modelAntiStandardGroupDataList;

    /**
     * 用户选择移除的标准数据
     */
    private List<RdeModelAntiFraudRuleGroupSnapshot> modelAntiStandardGroupDataDelList;

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
     * 当前需要复用的策略id
     */
    private Integer buildRuleId;
}
