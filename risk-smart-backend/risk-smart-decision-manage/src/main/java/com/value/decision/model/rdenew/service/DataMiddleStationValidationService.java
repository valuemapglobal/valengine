package com.value.decision.model.rdenew.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleMapper;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleSnapshotMapper;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScoreIndexRuleSnapshot;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.mapper.RdeModelDecisionCodeLevelMapper;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.value.decision.snapshot.mapper.RdeModelDecisionCodeLevelSnapshotMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DataMiddleStationValidationService {

    private static final Logger log = LoggerFactory.getLogger(DataMiddleStationValidationService.class);

    @Autowired
    private ScoreIndexRuleSnapshotMapper scoreIndexRuleSnapshotMapper;

    @Autowired
    private ScoreIndexRuleMapper scoreIndexRuleMapper;

    @Autowired
    private RdeModelDecisionCodeLevelSnapshotMapper rdeModelDecisionCodeLevelSnapshotMapper;

    @Autowired
    private RdeModelDecisionCodeLevelMapper rdeModelDecisionCodeLevelMapper;

    public boolean checkDataMiddleStationReference(String dataMiddleStationId) {
        if (StringUtils.isEmpty(dataMiddleStationId)) {
            return false;
        }

        try {

            // 检查评分规则原表
            LambdaQueryWrapper<ScoreIndexRule> scoreQueryWrapper = new LambdaQueryWrapper<>();
            scoreQueryWrapper.like(ScoreIndexRule::getConditionArray, dataMiddleStationId)
                    .eq(ScoreIndexRule::getDataState, 0)
                    .last("limit 1");
            long scoreCount = scoreIndexRuleMapper.selectCount(scoreQueryWrapper);

            // 检查评分规则快照表
            LambdaQueryWrapper<ScoreIndexRuleSnapshot> scoreQuerySnapshotWrapper = new LambdaQueryWrapper<>();
            scoreQuerySnapshotWrapper.like(ScoreIndexRuleSnapshot::getConditionArray, dataMiddleStationId)
                    .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                    .last("limit 1");
            long scoreSnapshotCount = scoreIndexRuleSnapshotMapper.selectCount(scoreQuerySnapshotWrapper);

            // 检查模型规则原表
            LambdaQueryWrapper<RdeModelDecisionCodeLevel> codeQueryWrapper = new LambdaQueryWrapper<>();
            codeQueryWrapper.like(RdeModelDecisionCodeLevel::getConditions, dataMiddleStationId)
                    .eq(RdeModelDecisionCodeLevel::getDataStatus, 0)
                    .last("limit 1");
            long codeCount = rdeModelDecisionCodeLevelMapper.selectCount(codeQueryWrapper);

            // 检查模型规则快照表
            LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot> codeQuerySnapshotWrapper = new LambdaQueryWrapper<>();
            codeQuerySnapshotWrapper.like(RdeModelDecisionCodeLevelSnapshot::getConditions, dataMiddleStationId)
                    .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0)
                    .last("limit 1");
            long codeSnapshotCount = rdeModelDecisionCodeLevelSnapshotMapper.selectCount(codeQuerySnapshotWrapper);

            return scoreCount > 0 || scoreSnapshotCount > 0 || codeCount > 0 || codeSnapshotCount > 0;
        } catch (Exception e) {
            log.error("检查DATA_MIDDLE_STATION标识符引用时发生异常", e);
            return false;
        }
    }
}
