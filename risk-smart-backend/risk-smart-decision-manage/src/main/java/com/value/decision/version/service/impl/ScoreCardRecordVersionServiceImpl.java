package com.value.decision.version.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.ScoreCardRecordMapper;
import com.value.decision.model.decisionmanage.mapper.ScoreIndexRuleMapper;
import com.value.decision.model.decisionmanage.mapper.ScorePrimaryIndexMapper;
import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScorePrimaryIndex;
import com.value.decision.version.common.util.StringUtil;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.domain.ScoreIndexRuleVersion;
import com.value.decision.version.domain.ScorePrimaryIndexVersion;
import com.value.decision.version.mapper.ModelVersionClassificationMapper;
import com.value.decision.version.mapper.ScoreCardRecordVersionMapper;
import com.value.decision.version.mapper.ScoreIndexRuleVersionMapper;
import com.value.decision.version.mapper.ScorePrimaryIndexVersionMapper;
import com.value.decision.version.service.ScoreCardRecordVersionService;
import com.value.decision.version.vo.VersionControlVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreCardRecordVersionServiceImpl extends ServiceImpl<ScoreCardRecordVersionMapper, ScoreCardRecordVersion> implements ScoreCardRecordVersionService {

    @Autowired
    private ScoreCardRecordVersionMapper scoreCardRecordVersionMapper;

    @Autowired
    private ScoreIndexRuleVersionMapper scoreIndexRuleVersionMapper;

    @Autowired
    private ScorePrimaryIndexVersionMapper scorePrimaryIndexVersionMapper;

    @Autowired
    private ScoreCardRecordMapper scoreCardRecordMapper;

    @Autowired
    private ScoreIndexRuleMapper scoreIndexRuleMapper;

    @Autowired
    private ScorePrimaryIndexMapper scorePrimaryIndexMapper;

    @Autowired
    private ModelVersionClassificationMapper modelVersionClassificationMapper;

    /**
     * 更新版本的通用方法
     * 评分模型版本控制
     *
     * 事务回滚
     * @param versionControlVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void versionControlScore(VersionControlVO versionControlVO){

            //**查询原表中此模型的全部数据
            //评分卡主表
            LambdaQueryWrapper<ScoreCardRecord> wrapperRecord = Wrappers.lambdaQuery();
            wrapperRecord.eq(ScoreCardRecord::getId,versionControlVO.getModelId())
                    .eq(ScoreCardRecord::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreCardRecord::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreCardRecord::getRuleCode,versionControlVO.getRuleCode());
            List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordMapper.selectList(wrapperRecord);
            //指标规则表
            LambdaQueryWrapper<ScoreIndexRule> wrapperRule = Wrappers.lambdaQuery();
            wrapperRule.eq(ScoreIndexRule::getScordCardId,versionControlVO.getModelId())
                    .eq(ScoreIndexRule::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreIndexRule::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreIndexRule::getRuleCode,versionControlVO.getRuleCode());
            List<ScoreIndexRule> scoreIndexRuleList = scoreIndexRuleMapper.selectList(wrapperRule);
            //一级指标存储表
            LambdaQueryWrapper<ScorePrimaryIndex> wrapperIndex = Wrappers.lambdaQuery();
            wrapperIndex.eq(ScorePrimaryIndex::getScoreCardId,versionControlVO.getModelId())
                    .eq(ScorePrimaryIndex::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScorePrimaryIndex::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScorePrimaryIndex::getRuleCode,versionControlVO.getRuleCode());
            List<ScorePrimaryIndex> scorePrimaryIndexList = scorePrimaryIndexMapper.selectList(wrapperIndex);

            //获取版本控制表中版本字段进行排序,此模型最新版本编号进行模型版本生成
            //最新的版本号
            String versionControlNew = null;
            LambdaQueryWrapper<ScoreCardRecordVersion> wrapperRecordVersion = Wrappers.lambdaQuery();
            wrapperRecordVersion.eq(ScoreCardRecordVersion::getId,versionControlVO.getModelId())
                    .eq(ScoreCardRecordVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreCardRecordVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreCardRecordVersion::getRuleCode,versionControlVO.getRuleCode());
            List<ScoreCardRecordVersion> scoreCardRecordVersionList = scoreCardRecordVersionMapper.selectList(wrapperRecordVersion);
            //排序后的模型版本表  取第一个为最新的版本号
            List<ScoreCardRecordVersion> antiFraudVersionList = scoreCardRecordVersionList.stream().sorted(Comparator.comparing(ScoreCardRecordVersion::getCreateTime).reversed()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(antiFraudVersionList)){
                versionControlNew = antiFraudVersionList.get(antiFraudVersionList.size()-1).getVersionControl();
                String finalVersionControlNew = versionControlNew;
                //最新版本号去通过版本生成方法生成修改后的版本号
                String versionNum = StringUtil.getVersionNum(finalVersionControlNew,versionControlVO.getProjectName(),versionControlVO.getBusinessName(),versionControlVO.getPersonOrCompany());
                scoreCardRecordList.stream().forEach(x -> x.setVersionControl(versionNum));
                scoreIndexRuleList.stream().forEach(x -> x.setVersionControl(versionNum));
                scorePrimaryIndexList.stream().forEach(x -> x.setVersionControl(versionNum));

                //将查询出的原表数据进行版本替换
                for (int i = 0; i < scoreCardRecordList.size(); i++) {
                    scoreCardRecordMapper.updateVersionList(scoreCardRecordList.get(i));
                }
                for (int i = 0; i < scoreIndexRuleList.size(); i++) {
                    scoreIndexRuleMapper.updateVersionList(scoreIndexRuleList.get(i));
                }
                for (int i = 0; i < scorePrimaryIndexList.size(); i++) {
                    scorePrimaryIndexMapper.updateVersionList(scorePrimaryIndexList.get(i));
                }
                //将版本表中该模型下所有版本的当前版本标识改为0
                ModelVersionClassification update = new ModelVersionClassification();
                update.setNewVersion(0);
                LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(ModelVersionClassification::getModelId,versionControlVO.getModelId())
                        .eq(ModelVersionClassification::getRuleCode,versionControlVO.getRuleCode())
                        .eq(ModelVersionClassification::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(ModelVersionClassification::getProjectCode,versionControlVO.getProjectCode());
                modelVersionClassificationMapper.update(update,wrapper);

                //版本归类表插入新纪录 只更新版本号字段
                ModelVersionClassification modelVersionClassification = new ModelVersionClassification();
                modelVersionClassification.setModelId(versionControlVO.getModelId());
                modelVersionClassification.setModelName(versionControlVO.getModelName());
                modelVersionClassification.setVersionControl(versionNum);
                modelVersionClassification.setRuleCode(versionControlVO.getRuleCode());
                modelVersionClassification.setBusinessCode(versionControlVO.getBusinessCode());
                modelVersionClassification.setProjectCode(versionControlVO.getProjectCode());
                modelVersionClassification.setCreateTime(new Date());
                modelVersionClassification.setDeptId(versionControlVO.getDeptId());
                modelVersionClassification.setNewVersion(1);
                modelVersionClassificationMapper.insert(modelVersionClassification);
            }

            //将新版本原表数据复制一份到版本控制表
            if (CollectionUtils.isNotEmpty(scoreCardRecordList)){
                List<ScoreCardRecordVersion> cardRecordVersionList = JSON.parseArray(JSON.toJSONString(scoreCardRecordList), ScoreCardRecordVersion.class);
                if (cardRecordVersionList != null) {
                    scoreCardRecordVersionMapper.insertBatch(cardRecordVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(scoreIndexRuleList)){
                List<ScoreIndexRuleVersion> scoreIndexRuleVersionList = JSON.parseArray(JSON.toJSONString(scoreIndexRuleList), ScoreIndexRuleVersion.class);
                if (scoreIndexRuleVersionList != null) {
                    scoreIndexRuleVersionMapper.insertBatch(scoreIndexRuleVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(scorePrimaryIndexList)){
                List<ScorePrimaryIndexVersion> scorePrimaryIndexVersionList = JSON.parseArray(JSON.toJSONString(scorePrimaryIndexList), ScorePrimaryIndexVersion.class);
                if (scorePrimaryIndexVersionList != null) {
                    scorePrimaryIndexVersionMapper.insertBatch(scorePrimaryIndexVersionList);
                }
            }



    }

    /**
     * 保留原版本的通用方法
     * 评分模型版本控制
     * 以防数据量太大插入缓慢 锁机制
     * 事务回滚
     * @param versionControlVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void versionReserveScore(VersionControlVO versionControlVO){

            //**查询原表中此模型的全部数据
            //评分卡主表
            List<ScoreCardRecord> scoreCardRecordList = new ArrayList<>();
            if (versionControlVO.getModelId() == null){
                LambdaQueryWrapper<ScoreCardRecord> wrapperRecord = Wrappers.lambdaQuery();
                wrapperRecord.eq(ScoreCardRecord::getScoreCard,versionControlVO.getModelName())
                        .eq(ScoreCardRecord::getProjectCode,versionControlVO.getProjectCode())
                        .eq(ScoreCardRecord::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(ScoreCardRecord::getRuleCode,versionControlVO.getRuleCode());
                scoreCardRecordList = scoreCardRecordMapper.selectList(wrapperRecord);
            }else {
                LambdaQueryWrapper<ScoreCardRecord> wrapperRecord = Wrappers.lambdaQuery();
                wrapperRecord.eq(ScoreCardRecord::getId,versionControlVO.getModelId())
                        .eq(ScoreCardRecord::getProjectCode,versionControlVO.getProjectCode())
                        .eq(ScoreCardRecord::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(ScoreCardRecord::getRuleCode,versionControlVO.getRuleCode());
                scoreCardRecordList = scoreCardRecordMapper.selectList(wrapperRecord);
            }
            //指标规则表
            LambdaQueryWrapper<ScoreIndexRule> wrapperRule = Wrappers.lambdaQuery();
            wrapperRule.eq(ScoreIndexRule::getScordCardId,versionControlVO.getModelId())
                    .eq(ScoreIndexRule::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreIndexRule::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreIndexRule::getRuleCode,versionControlVO.getRuleCode());
            List<ScoreIndexRule> scoreIndexRuleList = scoreIndexRuleMapper.selectList(wrapperRule);
            //一级指标存储表
            LambdaQueryWrapper<ScorePrimaryIndex> wrapperIndex = Wrappers.lambdaQuery();
            wrapperIndex.eq(ScorePrimaryIndex::getScoreCardId,versionControlVO.getModelId())
                    .eq(ScorePrimaryIndex::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScorePrimaryIndex::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScorePrimaryIndex::getRuleCode,versionControlVO.getRuleCode());
            List<ScorePrimaryIndex> scorePrimaryIndexList = scorePrimaryIndexMapper.selectList(wrapperIndex);

            //将版本控制表中此模型此版本下的全部数据
            //评分卡主表
            LambdaQueryWrapper<ScoreCardRecordVersion> wrapperRecordVersion = Wrappers.lambdaQuery();
            wrapperRecordVersion.eq(ScoreCardRecordVersion::getId,versionControlVO.getModelId())
                    .eq(ScoreCardRecordVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(ScoreCardRecordVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreCardRecordVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreCardRecordVersion::getRuleCode,versionControlVO.getRuleCode());
            scoreCardRecordVersionMapper.delete(wrapperRecordVersion);
            //指标规则表
            LambdaQueryWrapper<ScoreIndexRuleVersion> wrapperRuleVersion = Wrappers.lambdaQuery();
            wrapperRuleVersion.eq(ScoreIndexRuleVersion::getScordCardId,versionControlVO.getModelId())
                    .eq(ScoreIndexRuleVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(ScoreIndexRuleVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScoreIndexRuleVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScoreIndexRuleVersion::getRuleCode,versionControlVO.getRuleCode());
            scoreIndexRuleVersionMapper.delete(wrapperRuleVersion);
            //一级指标存储表
            LambdaQueryWrapper<ScorePrimaryIndexVersion> wrapperIndexVersion = Wrappers.lambdaQuery();
            wrapperIndexVersion.eq(ScorePrimaryIndexVersion::getScoreCardId,versionControlVO.getModelId())
                    .eq(ScorePrimaryIndexVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(ScorePrimaryIndexVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(ScorePrimaryIndexVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(ScorePrimaryIndexVersion::getRuleCode,versionControlVO.getRuleCode());
            scorePrimaryIndexVersionMapper.delete(wrapperIndexVersion);

            //将新版本原表数据复制一份到版本控制表
            if (CollectionUtils.isNotEmpty(scoreCardRecordList)){
                List<ScoreCardRecordVersion> cardRecordVersionList = JSON.parseArray(JSON.toJSONString(scoreCardRecordList), ScoreCardRecordVersion.class);
                if (cardRecordVersionList != null) {
                    scoreCardRecordVersionMapper.insertBatch(cardRecordVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(scoreIndexRuleList)){
                List<ScoreIndexRuleVersion> scoreIndexRuleVersionList = JSON.parseArray(JSON.toJSONString(scoreIndexRuleList), ScoreIndexRuleVersion.class);
                if (scoreIndexRuleVersionList != null) {
                    scoreIndexRuleVersionMapper.insertBatch(scoreIndexRuleVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(scorePrimaryIndexList)){
                List<ScorePrimaryIndexVersion> scorePrimaryIndexVersionList = JSON.parseArray(JSON.toJSONString(scorePrimaryIndexList), ScorePrimaryIndexVersion.class);
                if (scorePrimaryIndexVersionList != null) {
                    scorePrimaryIndexVersionMapper.insertBatch(scorePrimaryIndexVersionList);
                }
            }

//            // 抛出异常，模拟事务回滚
//            throw new RuntimeException("评分模型 Simulated Exception");
    }

}
