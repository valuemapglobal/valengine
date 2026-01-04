package com.value.decision.model.decisionmanage.model.dto.model;

import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import lombok.Data;

import java.util.List;

@Data
public class ScoreCardReuseVO {

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
     * 标准评分卡id
     */
    private Integer parentCardId;

    /**
     * 引用的标准评分卡数据
     */
    private List<ScoreCardRecord> parentCardData;

    /**
     * 版本控制
     */
    private String versionControl;

}
