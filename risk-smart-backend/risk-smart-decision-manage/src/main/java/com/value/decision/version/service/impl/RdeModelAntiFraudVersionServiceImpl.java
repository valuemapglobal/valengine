package com.value.decision.version.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.model.rdenew.mapper.RdeModelDecisionCodeLevelMapper;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudRuleRecordService;
import com.value.decision.version.common.util.StringUtil;
import com.value.decision.version.domain.*;
import com.value.decision.version.mapper.*;
import com.value.decision.version.service.RdeModelAntiFraudVersionService;
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
public class RdeModelAntiFraudVersionServiceImpl extends ServiceImpl<RdeModelAntiFraudVersionMapper, RdeModelAntiFraudVersion> implements RdeModelAntiFraudVersionService {

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
        private RdeModelAntiFraudRuleRecordService rdeModelAntiFraudRuleRecordService;

        @Autowired
        private RdeModelDecisionCodeLevelMapper rdeModelDecisionCodeLevelMapper;

        @Autowired
        private ModelVersionClassificationMapper modelVersionClassificationMapper;

        /**
         * 更新版本的通用方法
         * 规则模型版本控制
         *
         * 事务回滚
         * @param versionControlVO
         */
        @Transactional(rollbackFor = Exception.class)
        public void versionControlFraud(VersionControlVO versionControlVO){

                //**查询原表中此模型的全部数据
                //模型表
                LambdaQueryWrapper<RdeModelAntiFraud> wrapperFraud = Wrappers.lambdaQuery();
                wrapperFraud.eq(RdeModelAntiFraud::getId,versionControlVO.getModelId())
                        .eq(RdeModelAntiFraud::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraud::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraud::getRuleCode,versionControlVO.getRuleCode());
                List<RdeModelAntiFraud> rdeModelAntiFraudList = rdeModelAntiFraudMapper.selectList(wrapperFraud);
                //规则组表
                LambdaQueryWrapper<RdeModelAntiFraudRuleGroup> wrapperGroup = Wrappers.lambdaQuery();
                wrapperGroup.eq(RdeModelAntiFraudRuleGroup::getModelId,versionControlVO.getModelId())
                        .eq(RdeModelAntiFraudRuleGroup::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraudRuleGroup::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraudRuleGroup::getRuleCode,versionControlVO.getRuleCode());
                List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupMapper.selectList(wrapperGroup);
                //规则关联表
                LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> wrapperRecord = Wrappers.lambdaQuery();
                wrapperRecord.eq(RdeModelAntiFraudRuleRecord::getModelId,versionControlVO.getModelId())
                        .eq(RdeModelAntiFraudRuleRecord::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraudRuleRecord::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraudRuleRecord::getRuleCode,versionControlVO.getRuleCode());
                List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = rdeModelAntiFraudRuleRecordMapper.selectList(wrapperRecord);
                //规则code表  TODO
                List<Integer> codeIdList = rdeModelAntiFraudRuleRecordList.stream().map(x -> x.getCodeId()).collect(Collectors.toList());
                List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevelList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(codeIdList)){
                    LambdaQueryWrapper<RdeModelDecisionCodeLevel> wrapperLevel = Wrappers.lambdaQuery();
                    wrapperLevel.in(RdeModelDecisionCodeLevel::getId,codeIdList)
                            .eq(RdeModelDecisionCodeLevel::getProjectCode,versionControlVO.getProjectCode())
                            .eq(RdeModelDecisionCodeLevel::getBusinessCode,versionControlVO.getBusinessCode())
                            .eq(RdeModelDecisionCodeLevel::getRuleCode,versionControlVO.getRuleCode());
                    rdeModelDecisionCodeLevelList = rdeModelDecisionCodeLevelMapper.selectList(wrapperLevel);
                }

                //获取版本控制表中版本字段进行排序,此模型最新版本编号进行模型版本生成
                //最新的版本号
                String versionControlNew = null;
                LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapperFraudVersion = Wrappers.lambdaQuery();
                wrapperFraudVersion.eq(RdeModelAntiFraudVersion::getId,versionControlVO.getModelId())
                        .eq(RdeModelAntiFraudVersion::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraudVersion::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraudVersion::getRuleCode,versionControlVO.getRuleCode());
                List<RdeModelAntiFraudVersion> rdeModelAntiFraudVersionList = rdeModelAntiFraudVersionMapper.selectList(wrapperFraudVersion);
                //排序后的模型版本表  取第一个为最新的版本号
                List<RdeModelAntiFraudVersion> antiFraudVersionList = rdeModelAntiFraudVersionList.stream().sorted(Comparator.comparing(RdeModelAntiFraudVersion::getCreateTime).reversed()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(antiFraudVersionList)){
                    versionControlNew = antiFraudVersionList.get(antiFraudVersionList.size()-1).getVersionControl();
                    String finalVersionControlNew = versionControlNew;
                    //最新版本号去通过版本生成方法生成修改后的版本号  将查询出的原表数据进行版本替换
                    String versionNum = StringUtil.getVersionNum(finalVersionControlNew,versionControlVO.getProjectName(),versionControlVO.getBusinessName(),versionControlVO.getPersonOrCompany());
                    if (CollectionUtils.isNotEmpty(rdeModelAntiFraudList)){
                        rdeModelAntiFraudList.stream().forEach(x -> x.setVersionControl(versionNum));
                        for (int i = 0; i < rdeModelAntiFraudList.size(); i++) {
                            rdeModelAntiFraudMapper.updateVersionList(rdeModelAntiFraudList.get(i));
                        }
                    }

                    if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleGroupList)){
                        rdeModelAntiFraudRuleGroupList.stream().forEach(x -> x.setVersionControl(versionNum));
                        for (int i = 0; i < rdeModelAntiFraudList.size(); i++) {
                            rdeModelAntiFraudRuleGroupMapper.updateVersionList(rdeModelAntiFraudRuleGroupList.get(i));
                        }
                    }

                    if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleRecordList)){
                        rdeModelAntiFraudRuleRecordList.stream().forEach(x -> x.setVersionControl(versionNum));
                        for (int i = 0; i < rdeModelAntiFraudRuleRecordList.size(); i++) {
                            rdeModelAntiFraudRuleRecordMapper.updateVersionList(rdeModelAntiFraudRuleRecordList.get(i));
                        }
                    }

                    if (CollectionUtils.isNotEmpty(rdeModelDecisionCodeLevelList)){
                        rdeModelDecisionCodeLevelList.stream().forEach(x -> x.setVersionControl(versionNum));
                        for (int i = 0; i < rdeModelDecisionCodeLevelList.size(); i++) {
                            rdeModelDecisionCodeLevelMapper.updateVersionList(rdeModelDecisionCodeLevelList.get(i));
                        }
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
                if (CollectionUtils.isNotEmpty(rdeModelAntiFraudList)){
                    List<RdeModelAntiFraudVersion> modelAntiFraudVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudList), RdeModelAntiFraudVersion.class);
                    if (modelAntiFraudVersionList != null) {
                        rdeModelAntiFraudVersionMapper.insertBatch(modelAntiFraudVersionList);
                    }
                }
               if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleGroupList)){
                   List<RdeModelAntiFraudRuleGroupVersion> modelAntiFraudRuleGroupVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleGroupList), RdeModelAntiFraudRuleGroupVersion.class);
                   if (modelAntiFraudRuleGroupVersionList != null) {
                       rdeModelAntiFraudRuleGroupVersionMapper.insertBatch(modelAntiFraudRuleGroupVersionList);
                   }
               }
                if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleRecordList)){
                    List<RdeModelAntiFraudRuleRecordVersion> modelAntiFraudRuleRecordVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleRecordList), RdeModelAntiFraudRuleRecordVersion.class);
                    if (modelAntiFraudRuleRecordVersionList != null) {
                        rdeModelAntiFraudRuleRecordVersionMapper.insertBatch(modelAntiFraudRuleRecordVersionList);
                    }
                }
                if (CollectionUtils.isNotEmpty(rdeModelDecisionCodeLevelList)){
                    List<RdeModelDecisionCodeLevelVersion> modelDecisionCodeLevelVersionList = JSON.parseArray(JSON.toJSONString(rdeModelDecisionCodeLevelList), RdeModelDecisionCodeLevelVersion.class);
                    if (modelDecisionCodeLevelVersionList != null) {
                        rdeModelDecisionCodeLevelVersionMapper.insertBatch(modelDecisionCodeLevelVersionList);
                    }
                }
        }

    /**
     * 保留原版本的通用方法
     * 规则模型版本控制
     *
     * 事务回滚
     * @param versionControlVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void versionReserveFraud(VersionControlVO versionControlVO){

            //**查询原表中此模型的全部数据
            //模型表
            List<RdeModelAntiFraud> rdeModelAntiFraudList = new ArrayList<>();
            if (versionControlVO.getModelId() == null){
                LambdaQueryWrapper<RdeModelAntiFraud> wrapperFraud = Wrappers.lambdaQuery();
                wrapperFraud.eq(RdeModelAntiFraud::getName,versionControlVO.getModelName())
                        .eq(RdeModelAntiFraud::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraud::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraud::getRuleCode,versionControlVO.getRuleCode());
                rdeModelAntiFraudList = rdeModelAntiFraudMapper.selectList(wrapperFraud);
            }else {
                LambdaQueryWrapper<RdeModelAntiFraud> wrapperFraud = Wrappers.lambdaQuery();
                wrapperFraud.eq(RdeModelAntiFraud::getId,versionControlVO.getModelId())
                        .eq(RdeModelAntiFraud::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelAntiFraud::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelAntiFraud::getRuleCode,versionControlVO.getRuleCode());
                rdeModelAntiFraudList = rdeModelAntiFraudMapper.selectList(wrapperFraud);
            }
            //规则组表
            LambdaQueryWrapper<RdeModelAntiFraudRuleGroup> wrapperGroup = Wrappers.lambdaQuery();
            wrapperGroup.eq(RdeModelAntiFraudRuleGroup::getModelId,versionControlVO.getModelId())
                    .eq(RdeModelAntiFraudRuleGroup::getProjectCode,versionControlVO.getProjectCode())
                    .eq(RdeModelAntiFraudRuleGroup::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(RdeModelAntiFraudRuleGroup::getRuleCode,versionControlVO.getRuleCode());
            List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupMapper.selectList(wrapperGroup);
            //规则关联表
            LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> wrapperRecord = Wrappers.lambdaQuery();
            wrapperRecord.eq(RdeModelAntiFraudRuleRecord::getModelId,versionControlVO.getModelId())
                    .eq(RdeModelAntiFraudRuleRecord::getProjectCode,versionControlVO.getProjectCode())
                    .eq(RdeModelAntiFraudRuleRecord::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(RdeModelAntiFraudRuleRecord::getRuleCode,versionControlVO.getRuleCode());
            List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = rdeModelAntiFraudRuleRecordMapper.selectList(wrapperRecord);
            //规则code表  TODO
            List<Integer> codeIdList = rdeModelAntiFraudRuleRecordList.stream().map(x -> x.getCodeId()).collect(Collectors.toList());
            List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevelList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(codeIdList)){
                LambdaQueryWrapper<RdeModelDecisionCodeLevel> wrapperLevel = Wrappers.lambdaQuery();
                wrapperLevel.in(RdeModelDecisionCodeLevel::getId,codeIdList)
                        .eq(RdeModelDecisionCodeLevel::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelDecisionCodeLevel::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelDecisionCodeLevel::getRuleCode,versionControlVO.getRuleCode());
                rdeModelDecisionCodeLevelList = rdeModelDecisionCodeLevelMapper.selectList(wrapperLevel);
        }
            //将版本控制表中此模型此版本下的全部数据删除
            LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapperFraudVersion = Wrappers.lambdaQuery();
            wrapperFraudVersion.eq(RdeModelAntiFraudVersion::getId,versionControlVO.getModelId())
                    .eq(RdeModelAntiFraudVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(RdeModelAntiFraudVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(RdeModelAntiFraudVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(RdeModelAntiFraudVersion::getRuleCode,versionControlVO.getRuleCode());
            rdeModelAntiFraudVersionMapper.delete(wrapperFraudVersion);
            //规则组表
            LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperGroupVersion = Wrappers.lambdaQuery();
            wrapperGroupVersion.eq(RdeModelAntiFraudRuleGroupVersion::getModelId,versionControlVO.getModelId())
                    .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(RdeModelAntiFraudRuleGroupVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(RdeModelAntiFraudRuleGroupVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(RdeModelAntiFraudRuleGroupVersion::getRuleCode,versionControlVO.getRuleCode());
            rdeModelAntiFraudRuleGroupVersionMapper.delete(wrapperGroupVersion);
            //规则关联表
            LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRecordVersion = Wrappers.lambdaQuery();
            wrapperRecordVersion.eq(RdeModelAntiFraudRuleRecordVersion::getModelId,versionControlVO.getModelId())
                    .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,versionControlVO.getVersionControl())
                    .eq(RdeModelAntiFraudRuleRecordVersion::getProjectCode,versionControlVO.getProjectCode())
                    .eq(RdeModelAntiFraudRuleRecordVersion::getBusinessCode,versionControlVO.getBusinessCode())
                    .eq(RdeModelAntiFraudRuleRecordVersion::getRuleCode,versionControlVO.getRuleCode());
            rdeModelAntiFraudRuleRecordVersionMapper.delete(wrapperRecordVersion);
            //规则code表  TODO
            if (CollectionUtils.isNotEmpty(codeIdList)){
                LambdaQueryWrapper<RdeModelDecisionCodeLevelVersion> wrapperLevelVersion = Wrappers.lambdaQuery();
                wrapperLevelVersion.in(RdeModelDecisionCodeLevelVersion::getId,codeIdList)
                        .eq(RdeModelDecisionCodeLevelVersion::getVersionControl,versionControlVO.getVersionControl())
                        .eq(RdeModelDecisionCodeLevelVersion::getProjectCode,versionControlVO.getProjectCode())
                        .eq(RdeModelDecisionCodeLevelVersion::getBusinessCode,versionControlVO.getBusinessCode())
                        .eq(RdeModelDecisionCodeLevelVersion::getRuleCode,versionControlVO.getRuleCode());
                rdeModelDecisionCodeLevelVersionMapper.delete(wrapperLevelVersion);
            }
            //将修改后的此版本原表数据复制一份到版本控制表
            if (CollectionUtils.isNotEmpty(rdeModelAntiFraudList)){
                List<RdeModelAntiFraudVersion> modelAntiFraudVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudList), RdeModelAntiFraudVersion.class);
                if (modelAntiFraudVersionList != null) {
                    rdeModelAntiFraudVersionMapper.insertBatch(modelAntiFraudVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleGroupList)){
                List<RdeModelAntiFraudRuleGroupVersion> modelAntiFraudRuleGroupVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleGroupList), RdeModelAntiFraudRuleGroupVersion.class);
                if (modelAntiFraudRuleGroupVersionList != null) {
                    rdeModelAntiFraudRuleGroupVersionMapper.insertBatch(modelAntiFraudRuleGroupVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(rdeModelAntiFraudRuleRecordList)){
                List<RdeModelAntiFraudRuleRecordVersion> modelAntiFraudRuleRecordVersionList = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleRecordList), RdeModelAntiFraudRuleRecordVersion.class);
                if (modelAntiFraudRuleRecordVersionList != null) {
                    rdeModelAntiFraudRuleRecordVersionMapper.insertBatch(modelAntiFraudRuleRecordVersionList);
                }
            }
            if (CollectionUtils.isNotEmpty(rdeModelDecisionCodeLevelList)){
                List<RdeModelDecisionCodeLevelVersion> modelDecisionCodeLevelVersionList = JSON.parseArray(JSON.toJSONString(rdeModelDecisionCodeLevelList), RdeModelDecisionCodeLevelVersion.class);
                if (modelDecisionCodeLevelVersionList != null) {
                    rdeModelDecisionCodeLevelVersionMapper.insertBatch(modelDecisionCodeLevelVersionList);
                }
            }

//            // 抛出异常，模拟事务回滚
//            throw new RuntimeException("规则模型 Simulated Exception");
    }
}
