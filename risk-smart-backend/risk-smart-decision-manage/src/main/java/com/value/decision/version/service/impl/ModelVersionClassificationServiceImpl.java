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
import com.value.decision.model.decisionmanage.service.IScoreCardRecordService;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.model.rdenew.mapper.RdeModelDecisionCodeLevelMapper;
import com.value.decision.snapshot.service.RdeModelAntiFraudSnapshotService;
import com.value.decision.snapshot.vo.RuleReleaseVO;
import com.value.decision.version.domain.*;
import com.value.decision.version.dto.VersionControlDTO;
import com.value.decision.version.mapper.*;
import com.value.decision.version.service.ModelVersionClassificationService;
import com.value.decision.version.vo.ModelVersionVO;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModelVersionClassificationServiceImpl extends ServiceImpl<ModelVersionClassificationMapper, ModelVersionClassification> implements ModelVersionClassificationService {

    @Autowired
    private RdeModelAntiFraudVersionMapper rdeModelAntiFraudVersionMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupVersionMapper rdeModelAntiFraudRuleGroupVersionMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordVersionMapper rdeModelAntiFraudRuleRecordVersionMapper;

    @Autowired
    private RdeModelDecisionCodeLevelVersionMapper rdeModelDecisionCodeLevelVersionMapper;

    @Autowired
    private RdeModelAntiFraudMapper rdeModelAntiFraudMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupMapper rdeModelAntiFraudRuleGroupMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordMapper rdeModelAntiFraudRuleRecordMapper;

    @Autowired
    private RdeModelDecisionCodeLevelMapper rdeModelDecisionCodeLevelMapper;

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

    @Autowired
    private RdeModelAntiFraudSnapshotService rdeModelAntiFraudSnapshotService;

    @Autowired
    private IScoreCardRecordService scoreCardRecordService;

    /**
     * 根据模型id能查出全部版本号
     * @param versionControlVO
     * @return
     */
    public List<VersionControlDTO> getVersion(VersionControlVO versionControlVO){
        List<VersionControlDTO> listVersion = new ArrayList<>();
        VersionControlDTO versionControlDTO = new VersionControlDTO();

        LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelVersionClassification::getDataStatus,0)
                .eq(ModelVersionClassification::getDeptId,versionControlVO.getDeptId())
                .eq(ModelVersionClassification::getBusinessCode,versionControlVO.getBusinessCode())
                .eq(ModelVersionClassification::getProjectCode,versionControlVO.getProjectCode())
                .eq(ModelVersionClassification::getRuleCode,versionControlVO.getRuleCode());
        List<ModelVersionClassification> list = modelVersionClassificationMapper.selectList(wrapper);
        //按照modelId分组
        Map<Integer, List<ModelVersionClassification>> collect =
                list.stream().collect(Collectors.groupingBy(ModelVersionClassification::getModelId));
        for (Map.Entry<Integer, List<ModelVersionClassification>> entry : collect.entrySet()){
            List<ModelVersionClassification> valueList = entry.getValue();
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.get(i).getNewVersion() == 1){
                    versionControlDTO.setNewVersion(valueList.get(i).getModelName());
                }
                if (valueList.get(i).getUserVersion() == 1){
                    versionControlDTO.setUserVersion(valueList.get(i).getModelName());
                }
                versionControlDTO.setChampionVersion(valueList.get(i).getChampionVersion() == null?0:valueList.get(i).getChampionVersion());
            }
            versionControlDTO.setModelId(versionControlVO.getModelId());
            listVersion.add(versionControlDTO);
        }

        return listVersion;
    }

    /**
     * 冠军标识插入以及更新发布此版本方法编写
     * @param modelVersionVO
     */
      @Transactional(rollbackFor = Exception.class)
      public void championLogoVersion(ModelVersionVO modelVersionVO, LoginUser loginUser){

          //将版本表中该模型下全部版本的冠军标识先全部改为0
          ModelVersionClassification update = new ModelVersionClassification();
          update.setChampionVersion(0);
          update.setUserVersion(0);
          LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
          wrapper.eq(ModelVersionClassification::getModelId,modelVersionVO.getModelId())
                  .eq(ModelVersionClassification::getRuleCode,modelVersionVO.getRuleCode())
                  .eq(ModelVersionClassification::getBusinessCode,modelVersionVO.getBusinessCode())
                  .eq(ModelVersionClassification::getProjectCode,modelVersionVO.getProjectCode());
          modelVersionClassificationMapper.update(update,wrapper);

          if (modelVersionVO.getNewVersion().equals(modelVersionVO.getChampionVersion())){
              //将版本表中该模型下此版本的冠军标识改为1
              ModelVersionClassification updateVersion = new ModelVersionClassification();
              updateVersion.setChampionVersion(1);
              LambdaQueryWrapper<ModelVersionClassification> wrapperVersion = Wrappers.lambdaQuery();
              wrapperVersion.eq(ModelVersionClassification::getModelId,modelVersionVO.getModelId())
                      .eq(ModelVersionClassification::getRuleCode,modelVersionVO.getRuleCode())
                      .eq(ModelVersionClassification::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(ModelVersionClassification::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(ModelVersionClassification::getVersionControl,modelVersionVO.getChampionVersion());  //冠军版本
              modelVersionClassificationMapper.update(updateVersion,wrapperVersion);
          }
          //将版本表中该模型下此版本的使用版本改为1
          ModelVersionClassification updateVer = new ModelVersionClassification();
          updateVer.setUserVersion(1);
          LambdaQueryWrapper<ModelVersionClassification> wrapperVer = Wrappers.lambdaQuery();
          wrapperVer.eq(ModelVersionClassification::getModelId,modelVersionVO.getModelId())
                  .eq(ModelVersionClassification::getRuleCode,modelVersionVO.getRuleCode())
                  .eq(ModelVersionClassification::getBusinessCode,modelVersionVO.getBusinessCode())
                  .eq(ModelVersionClassification::getProjectCode,modelVersionVO.getProjectCode())
                  .eq(ModelVersionClassification::getVersionControl,modelVersionVO.getUserVersion());  //使用版本
          modelVersionClassificationMapper.update(updateVer,wrapperVer);



          if ("1".equals(modelVersionVO.getRuleCode())){  //评分模型
              //根据此模型id和版本号在版本控制表中找出全部数据
              //评分卡主表
              LambdaQueryWrapper<ScoreCardRecordVersion> wrapperRecord = Wrappers.lambdaQuery();
              wrapperRecord.eq(ScoreCardRecordVersion::getId,modelVersionVO.getModelId())
                      .eq(ScoreCardRecordVersion::getVersionControl,modelVersionVO.getUserVersion())  //选择当前使用版本
                      .eq(ScoreCardRecordVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(ScoreCardRecordVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(ScoreCardRecordVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<ScoreCardRecordVersion> scoreCardRecordVersionList = scoreCardRecordVersionMapper.selectList(wrapperRecord);
              //指标规则表
              LambdaQueryWrapper<ScoreIndexRuleVersion> wrapperRule = Wrappers.lambdaQuery();
              wrapperRule.eq(ScoreIndexRuleVersion::getScordCardId,modelVersionVO.getModelId())
                      .eq(ScoreIndexRuleVersion::getVersionControl,modelVersionVO.getUserVersion())
                      .eq(ScoreIndexRuleVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(ScoreIndexRuleVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(ScoreIndexRuleVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<ScoreIndexRuleVersion> scoreIndexRuleVersionList = scoreIndexRuleVersionMapper.selectList(wrapperRule);
              //一级指标存储表
              LambdaQueryWrapper<ScorePrimaryIndexVersion> wrapperIndex = Wrappers.lambdaQuery();
              wrapperIndex.eq(ScorePrimaryIndexVersion::getScoreCardId,modelVersionVO.getModelId())
                      .eq(ScorePrimaryIndexVersion::getVersionControl,modelVersionVO.getUserVersion())
                      .eq(ScorePrimaryIndexVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(ScorePrimaryIndexVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(ScorePrimaryIndexVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<ScorePrimaryIndexVersion> scorePrimaryIndexVersionList = scorePrimaryIndexVersionMapper.selectList(wrapperIndex);
              //将原表此模型下的数据删除(原表中一个模型下只存在一个版本的数据,所以不需要再将版本号作为筛选条件) ,将上述查询出的数据存入原表

              //将修改后的此版本原表数据复制一份到版本控制表
              if (CollectionUtils.isNotEmpty(scoreCardRecordVersionList)){
                  List<ScoreCardRecord> cardRecordList = JSON.parseArray(JSON.toJSONString(scoreCardRecordVersionList), ScoreCardRecord.class);
                  if (cardRecordList != null) {
//                      scoreCardRecordMapper.insertBatch(cardRecordList);
                      for (int i = 0; i < cardRecordList.size(); i++) {
                          scoreCardRecordMapper.updateById(cardRecordList.get(i));
                      }
                  }
              }
              if (CollectionUtils.isNotEmpty(scoreIndexRuleVersionList)){
                  List<ScoreIndexRule> scoreIndexRuleList = JSON.parseArray(JSON.toJSONString(scoreIndexRuleVersionList), ScoreIndexRule.class);
                  if (scoreIndexRuleList != null) {
//                      scoreIndexRuleMapper.insertBatch(scoreIndexRuleList);
                      for (int i = 0; i < scoreIndexRuleList.size(); i++) {
                          scoreIndexRuleMapper.updateById(scoreIndexRuleList.get(i));
                      }
                  }
              }
              if (CollectionUtils.isNotEmpty(scorePrimaryIndexVersionList)){
                  List<ScorePrimaryIndex> scorePrimaryIndexList = JSON.parseArray(JSON.toJSONString(scorePrimaryIndexVersionList), ScorePrimaryIndex.class);
                  if (scorePrimaryIndexList != null) {
//                      scorePrimaryIndexMapper.insertBatch(scorePrimaryIndexList);
                      for (int i = 0; i < scorePrimaryIndexList.size(); i++) {
                          scorePrimaryIndexMapper.updateById(scorePrimaryIndexList.get(i));
                      }
                  }
              }

              //在调用发布方法,将发布表中此模型数据删除 将原表数据存入
              ScoreCardRecord scoreCardRecord = new ScoreCardRecord();
              scoreCardRecord.setVersionControl(modelVersionVO.getUserVersion()); //当前使用版本号
              scoreCardRecord.setBusinessCode(modelVersionVO.getBusinessCode());
              scoreCardRecord.setProjectCode(modelVersionVO.getProjectCode());
              scoreCardRecord.setRuleCode(modelVersionVO.getRuleCode());
              scoreCardRecord.setId(modelVersionVO.getModelId());
              scoreCardRecordService.release(scoreCardRecord,loginUser);

          }else if ("5".equals(modelVersionVO.getRuleCode())){ //规则模型
              //根据此模型id和版本号在版本控制表中找出全部数据
              //模型表
              LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapperFraud = Wrappers.lambdaQuery();
              wrapperFraud.eq(RdeModelAntiFraudVersion::getId,modelVersionVO.getModelId())
                      .eq(RdeModelAntiFraudVersion::getVersionControl,modelVersionVO.getUserVersion()) //选择为当前使用的版本号
                      .eq(RdeModelAntiFraudVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(RdeModelAntiFraudVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(RdeModelAntiFraudVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<RdeModelAntiFraudVersion> rdeModelAntiFraudVersionList = rdeModelAntiFraudVersionMapper.selectList(wrapperFraud);
              //规则组表
              LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperGroup = Wrappers.lambdaQuery();
              wrapperGroup.eq(RdeModelAntiFraudRuleGroupVersion::getModelId,modelVersionVO.getModelId())
                      .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,modelVersionVO.getUserVersion())
                      .eq(RdeModelAntiFraudRuleGroupVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(RdeModelAntiFraudRuleGroupVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(RdeModelAntiFraudRuleGroupVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<RdeModelAntiFraudRuleGroupVersion> rdeModelAntiFraudRuleGroupVersionList = rdeModelAntiFraudRuleGroupVersionMapper.selectList(wrapperGroup);
              //规则关联表
              LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRecord = Wrappers.lambdaQuery();
              wrapperRecord.eq(RdeModelAntiFraudRuleRecordVersion::getModelId,modelVersionVO.getModelId())
                      .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,modelVersionVO.getUserVersion())
                      .eq(RdeModelAntiFraudRuleRecordVersion::getProjectCode,modelVersionVO.getProjectCode())
                      .eq(RdeModelAntiFraudRuleRecordVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                      .eq(RdeModelAntiFraudRuleRecordVersion::getRuleCode,modelVersionVO.getRuleCode());
              List<RdeModelAntiFraudRuleRecordVersion> rdeModelAntiFraudRuleRecordVersionList = rdeModelAntiFraudRuleRecordVersionMapper.selectList(wrapperRecord);
              //规则code表  TODO
              List<RdeModelDecisionCodeLevelVersion> rdeModelDecisionCodeLevelVersionList = new ArrayList<>();
              List<Integer> codeIdList = rdeModelAntiFraudRuleRecordVersionList.stream().map(x -> x.getCodeId()).collect(Collectors.toList());
              if (CollectionUtils.isNotEmpty(codeIdList)){
                  LambdaQueryWrapper<RdeModelDecisionCodeLevelVersion> wrapperLevel = Wrappers.lambdaQuery();
                  wrapperLevel.in(RdeModelDecisionCodeLevelVersion::getId,codeIdList)
                          .eq(RdeModelDecisionCodeLevelVersion::getVersionControl,modelVersionVO.getUserVersion())
                          .eq(RdeModelDecisionCodeLevelVersion::getProjectCode,modelVersionVO.getProjectCode())
                          .eq(RdeModelDecisionCodeLevelVersion::getBusinessCode,modelVersionVO.getBusinessCode())
                          .eq(RdeModelDecisionCodeLevelVersion::getRuleCode,modelVersionVO.getRuleCode());
                  rdeModelDecisionCodeLevelVersionList = rdeModelDecisionCodeLevelVersionMapper.selectList(wrapperLevel);
              }

              //将修改后的此版本原表数据复制一份到版本控制表
              if (CollectionUtils.isNotEmpty(rdeModelAntiFraudVersionList)){
                  List<RdeModelAntiFraud> modelAntiFraudVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudVersionList), RdeModelAntiFraud.class);
                  if (modelAntiFraudVersionList != null) {
                      for (int i = 0; i < modelAntiFraudVersionList.size(); i++) {
                          rdeModelAntiFraudMapper.updateById(modelAntiFraudVersionList.get(i));
                      }
                  }
              }
              if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleGroupVersionList)){
                  List<RdeModelAntiFraudRuleGroup> modelAntiFraudRuleGroupVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleGroupVersionList), RdeModelAntiFraudRuleGroup.class);
                  if (modelAntiFraudRuleGroupVersionList != null) {
                      for (int i = 0; i < modelAntiFraudRuleGroupVersionList.size(); i++) {
                          rdeModelAntiFraudRuleGroupMapper.updateById(modelAntiFraudRuleGroupVersionList.get(i));
                      }
                  }
              }
              if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleRecordVersionList)){
                  List<RdeModelAntiFraudRuleRecord> modelAntiFraudRuleRecordVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleRecordVersionList), RdeModelAntiFraudRuleRecord.class);
                  if (modelAntiFraudRuleRecordVersionList != null) {
                      for (int i = 0; i < modelAntiFraudRuleRecordVersionList.size(); i++) {
                          rdeModelAntiFraudRuleRecordMapper.updateById(modelAntiFraudRuleRecordVersionList.get(i));
                      }
                  }
              }
              if (CollectionUtils.isNotEmpty(rdeModelDecisionCodeLevelVersionList)){
                  List<RdeModelDecisionCodeLevel> modelDecisionCodeLevelList = JSON.parseArray(JSON.toJSONString(rdeModelDecisionCodeLevelVersionList), RdeModelDecisionCodeLevel.class);
                  if (modelDecisionCodeLevelList != null) {
//                      rdeModelDecisionCodeLevelMapper.insertBatch(modelDecisionCodeLevelList);
                      for (int i = 0; i < modelDecisionCodeLevelList.size(); i++) {
                          rdeModelDecisionCodeLevelMapper.updateById(modelDecisionCodeLevelList.get(i));
                      }
                  }
              }
              //在调用发布方法,将发布表中此模型数据删除 将原表数据存入
                  RuleReleaseVO ruleReleaseVO = new RuleReleaseVO();
                  ruleReleaseVO.setBusinessCode(modelVersionVO.getBusinessCode());
                  ruleReleaseVO.setProjectCode(modelVersionVO.getProjectCode());
                  ruleReleaseVO.setRuleCode(modelVersionVO.getRuleCode());
                  List<Integer> list = new ArrayList<>();
                  list.add(modelVersionVO.getModelId());
                  ruleReleaseVO.setModelIdList(list);
                  rdeModelAntiFraudSnapshotService.ruleDataRule(ruleReleaseVO.getModelIdList(),modelVersionVO.getAuthorization(),loginUser.getSysUser(),ruleReleaseVO.getProjectCode(),ruleReleaseVO.getRuleCode(),ruleReleaseVO.getBusinessCode());


          }

      }

}
