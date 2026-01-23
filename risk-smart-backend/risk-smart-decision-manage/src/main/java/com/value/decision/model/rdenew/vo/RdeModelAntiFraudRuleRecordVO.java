package com.value.decision.model.rdenew.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.value.decision.model.decisionmanage.model.vo.GenerateRuleVO;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelRuleActivityRecord;
import com.value.decision.model.rdenew.domain.RdeModelRuleMethod;
import com.value.decision.model.rdenew.domain.RdeModelRuleProperty;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import lombok.Data;

import java.util.List;

@Data
public class RdeModelAntiFraudRuleRecordVO extends RdeModelAntiFraudRuleRecord {

    private Integer modelId;

    private Integer isStart;
    
    private String content;

    private List<RdeModelRuleActivityRecord> termArray;

    private List<RdeModelRuleProperty> propertyArray;

    private List<RdeModelRuleMethod> methodArray;

    @TableField(exist = false)
    private GenerateRuleVO generateRuleVO;

    private Integer pageNum;
    private Integer pageSize;

    /**
     * 用户选择新增的数据
     */
    private List<RdeModelAntiFraudRuleRecord> modelAntiRecordDataList;

    /**
     * 用户选择移除的数据
     */
    private List<RdeModelAntiFraudRuleRecord> modelAntiRecordDataDelList;

    /**
     * 用户选择新增的标准数据
     */
    private List<RdeModelAntiFraudRuleRecordSnapshot> modelAntiStandardRecordDataList;

    /**
     * 用户选择移除的标准数据
     */
    private List<RdeModelAntiFraudRuleRecordSnapshot> modelAntiStandardRecordDataDelList;

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

    /**
     * 当前需要复用的规则组id
     */
    private Integer buildGroupId;
    

}
