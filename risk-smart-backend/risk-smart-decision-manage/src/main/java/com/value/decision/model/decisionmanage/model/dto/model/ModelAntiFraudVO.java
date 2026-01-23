package com.value.decision.model.decisionmanage.model.dto.model;

import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ModelAntiFraudVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 数据状态
     */
    private Byte dataStatus;


    /**
     * 页面标识 分类为1,规则为0
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
    private List<RdeModelAntiFraud> modelAntiDataList;

    /**
     * 用户选择移除的数据
     */
    private List<RdeModelAntiFraud> modelAntiDataDelList;

    /**
     * 用户选择新增的标准数据
     */
    private List<RdeModelAntiFraudSnapshot> modelAntiStandardDataList;

    /**
     * 用户选择移除的标准数据
     */
    private List<RdeModelAntiFraudSnapshot> modelAntiStandardDataDelList;

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
}
