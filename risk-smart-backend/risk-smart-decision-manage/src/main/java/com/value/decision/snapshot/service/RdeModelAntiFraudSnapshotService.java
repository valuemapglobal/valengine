package com.value.decision.snapshot.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.value.decision.model.decisionmanage.model.RuleRecordReuseSnapshot;
import com.value.decision.model.decisionmanage.service.IRuleRecordReuseSnapshotService;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.model.rdenew.mapper.RdeModelDecisionCodeLevelMapper;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.value.decision.snapshot.mapper.*;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.mapper.ModelVersionClassificationMapper;
import com.risksmart.system.domain.SysUser;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 反欺诈模型表 服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@Log4j2
public class RdeModelAntiFraudSnapshotService {
    private Logger logger = LoggerFactory.getLogger(RdeModelAntiFraudSnapshotService.class);

    @Autowired
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper rdeModelAntiFraudRuleRecordSnapshotMapper;

    @Autowired
    private RdeModelDecisionCodeLevelSnapshotMapper rdeModelDecisionCodeLevelSnapshotMapper;

    @Autowired
    RdeModelTestResultMapper rdeModelTestResultMapper;
    @Autowired
    private RdeModelAntiFraudMapper rdeModelAntiFraudMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupSnapshotMapper rdeModelAntiFraudRuleGroupSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupMapper rdeModelAntiFraudRuleGroupMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordMapper rdeModelAntiFraudRuleRecordMapper;

    @Autowired
    private RdeModelDecisionCodeLevelMapper rdeModelDecisionCodeLevelMapper;

    @Autowired
    private IRuleRecordReuseSnapshotService ruleRecordReuseSnapshotService;

    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;

    @Autowired
    private ModelVersionClassificationMapper modelVersionClassificationMapper;

    /**
     * 模型策略发布
     * @param modelIdList  当前开启的需存储在快照表中,还有未开启的所以需用部门来查找进行清除快照表
     */
    @Async("myAsync")
    @Transactional
    public Future<Boolean> ruleDataRule(List<Integer> modelIdList, String authorization, SysUser user, String projectCode, String ruleCode,String businessCode){
        try {
            logger.info("策略发布方法：当前用户为：" + user);
            //获取此账户下的userId和deptId  TODO 测试时固定为101 测试完放开
            int userId = user.getUserId().intValue();
            int deptId = user.getDeptId().intValue();


            //根据modelId以及deptId在关联表中查找此模型是否已发布过
            if (CollUtil.isNotEmpty(modelIdList)){
                //未发布过 直接从原表中获取数据存入快照表
                dataMigration(modelIdList,deptId,projectCode,ruleCode,businessCode);
            }

            //根据模型编号查询出模型信息
            LambdaQueryWrapper<RdeModelAntiFraud> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.in(RdeModelAntiFraud::getId,modelIdList)
                    .eq(RdeModelAntiFraud::getDataStatus,0)
                    .eq(RdeModelAntiFraud::getStatus,1)
                    .eq(RdeModelAntiFraud::getDeptId,deptId)
                    .eq(RdeModelAntiFraud::getProjectCode,projectCode)
                    .eq(RdeModelAntiFraud::getBusinessCode,businessCode)
                    .eq(RdeModelAntiFraud::getRuleCode,ruleCode);
            List<RdeModelAntiFraud> rdeModelAntiFrauds = rdeModelAntiFraudMapper.selectList(wrapper1);
            //发布后显示当前正在使用的模型版本
            if (!CollectionUtils.isEmpty(rdeModelAntiFrauds)){
                for (int i = 0; i < rdeModelAntiFrauds.size(); i++) {
                    //将版本表中该模型下所有版本的当前版本标识改为0
                    ModelVersionClassification update = new ModelVersionClassification();
                    update.setUserVersion(0);
                    LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
                    wrapper.eq(ModelVersionClassification::getModelId,rdeModelAntiFrauds.get(i).getId())
                            .eq(ModelVersionClassification::getRuleCode,rdeModelAntiFrauds.get(i).getRuleCode())
                            .eq(ModelVersionClassification::getBusinessCode,rdeModelAntiFrauds.get(i).getBusinessCode())
                            .eq(ModelVersionClassification::getProjectCode,rdeModelAntiFrauds.get(i).getProjectCode());
                    modelVersionClassificationMapper.update(update,wrapper);

                    //将此版本改为1
                    ModelVersionClassification updateVersion = new ModelVersionClassification();
                    updateVersion.setUserVersion(1);
                    LambdaQueryWrapper<ModelVersionClassification> wrapperVersion = Wrappers.lambdaQuery();
                    wrapperVersion.eq(ModelVersionClassification::getModelId,rdeModelAntiFrauds.get(i).getId())
                            .eq(ModelVersionClassification::getVersionControl,rdeModelAntiFrauds.get(i).getVersionControl())
                            .eq(ModelVersionClassification::getRuleCode,rdeModelAntiFrauds.get(i).getRuleCode())
                            .eq(ModelVersionClassification::getBusinessCode,rdeModelAntiFrauds.get(i).getBusinessCode())
                            .eq(ModelVersionClassification::getProjectCode,rdeModelAntiFrauds.get(i).getProjectCode());
                    modelVersionClassificationMapper.update(updateVersion,wrapperVersion);
                }
            }

            return new AsyncResult<>(true);
        }catch (Exception e){
            logger.error("模型测试发布失败");
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    @Transactional
    public void dataMigration(List<Integer> modelIdList,Integer deptId, String projectCode,String ruleCode,String businessCode){
        //1.模型表
        List<RdeModelAntiFraud> rdeModelAntiFrauds = rdeModelAntiFraudMapper.queryList2(modelIdList,deptId,projectCode,ruleCode,businessCode);
        List<RdeModelAntiFraudSnapshot> rdeModelAntiFraudSnapshots = new ArrayList<>();
        rdeModelAntiFraudSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelAntiFrauds),RdeModelAntiFraudSnapshot.class);
        if (CollUtil.isNotEmpty(rdeModelAntiFraudSnapshots)){
            rdeModelAntiFraudSnapshotMapper.delete(new LambdaQueryWrapper<RdeModelAntiFraudSnapshot>()
                    .eq(RdeModelAntiFraudSnapshot::getDeptId,deptId)
                    .eq(RdeModelAntiFraudSnapshot::getProjectCode,projectCode)
                    .eq(RdeModelAntiFraudSnapshot::getBusinessCode,businessCode)
                    .eq(RdeModelAntiFraudSnapshot::getRuleCode,ruleCode));
            for (RdeModelAntiFraudSnapshot rdeModelAntiFraudSnapshot : rdeModelAntiFraudSnapshots) {
                rdeModelAntiFraudSnapshotMapper.insert(rdeModelAntiFraudSnapshot);
            }

        }
        //2.策略组表
        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.queryList2(modelIdList,deptId,projectCode,businessCode,ruleCode);
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupSnapshots = new ArrayList<>();
        rdeModelAntiFraudRuleGroupSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleGroups),RdeModelAntiFraudRuleGroupSnapshot.class);
        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleGroupSnapshots)){
            rdeModelAntiFraudRuleGroupSnapshotMapper.delete(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot>()
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getDeptId,deptId)
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getProjectCode,projectCode)
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getBusinessCode,businessCode)
                    .eq(RdeModelAntiFraudRuleGroupSnapshot::getRuleCode,ruleCode));
            for (RdeModelAntiFraudRuleGroupSnapshot rdeModelAntiFraudRuleGroupSnapshot : rdeModelAntiFraudRuleGroupSnapshots) {
                rdeModelAntiFraudRuleGroupSnapshotMapper.insert(rdeModelAntiFraudRuleGroupSnapshot);
            }

        }
        //3.策略表
        List<Integer> GroupIdList = rdeModelAntiFraudRuleGroups.stream().map(RdeModelAntiFraudRuleGroup::getId).collect(Collectors.toList());
        List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecords = rdeModelAntiFraudRuleRecordMapper.queryList2(GroupIdList,deptId,projectCode,businessCode,ruleCode);
        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudRuleRecordSnapshots = new ArrayList<>();
        rdeModelAntiFraudRuleRecordSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelRuleRecords),RdeModelAntiFraudRuleRecordSnapshot.class);
        rdeModelAntiFraudRuleRecordSnapshotMapper.delete(new LambdaQueryWrapper<RdeModelAntiFraudRuleRecordSnapshot>()
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getDeptId,deptId)
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getProjectCode,projectCode)
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getBusinessCode,businessCode)
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getRuleCode,ruleCode));
        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleRecordSnapshots)){
            for (RdeModelAntiFraudRuleRecordSnapshot rdeModelAntiFraudRuleRecordSnapshot : rdeModelAntiFraudRuleRecordSnapshots) {
                rdeModelAntiFraudRuleRecordSnapshotMapper.insert(rdeModelAntiFraudRuleRecordSnapshot);
            }

        }

        //4.规则表
        List<Integer> codeIdList = rdeModelRuleRecords.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList());
        List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevels = rdeModelDecisionCodeLevelMapper.queryList2(codeIdList,deptId,projectCode,ruleCode,businessCode);
        List<RdeModelDecisionCodeLevelSnapshot> rdeModelDecisionCodeLevelSnapshots = new ArrayList<>();
        rdeModelDecisionCodeLevelSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelDecisionCodeLevels),RdeModelDecisionCodeLevelSnapshot.class);
        rdeModelDecisionCodeLevelSnapshotMapper.delete(new LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot>()
                .eq(RdeModelDecisionCodeLevelSnapshot::getDeptId,deptId)
                .eq(RdeModelDecisionCodeLevelSnapshot::getProjectCode,projectCode)
                .eq(RdeModelDecisionCodeLevelSnapshot::getBusinessCode,businessCode)
                .eq(RdeModelDecisionCodeLevelSnapshot::getRuleCode,ruleCode));
        if (CollUtil.isNotEmpty(rdeModelDecisionCodeLevelSnapshots)){
            for (RdeModelDecisionCodeLevelSnapshot rdeModelDecisionCodeLevelSnapshot : rdeModelDecisionCodeLevelSnapshots) {
                rdeModelDecisionCodeLevelSnapshotMapper.insert(rdeModelDecisionCodeLevelSnapshot);
            }

        }

        //关联表数据
        LambdaQueryWrapper<RuleRecordReuse> eq3 = new LambdaQueryWrapper<RuleRecordReuse>()
                .eq(RuleRecordReuse::getDeptId, deptId)
                .eq(RuleRecordReuse::getBuildProjectCode, projectCode)
                .eq(RuleRecordReuse::getBuildBusinessCode, businessCode)
                .eq(RuleRecordReuse::getBuildRuleCode, ruleCode)
                .eq(RuleRecordReuse::getDeptId,deptId)
//                .in(RuleRecordReuse::getParentCardId,modelIdList)
                .eq(RuleRecordReuse::getDataStatus,0);
        LambdaQueryWrapper<RuleRecordReuseSnapshot> eqS3 = new LambdaQueryWrapper<RuleRecordReuseSnapshot>()
                .eq(RuleRecordReuseSnapshot::getDeptId, deptId)
                .eq(RuleRecordReuseSnapshot::getBuildProjectCode, projectCode)
                .eq(RuleRecordReuseSnapshot::getBuildBusinessCode,businessCode)
                .eq(RuleRecordReuseSnapshot::getBuildRuleCode, ruleCode)
                .eq(RuleRecordReuseSnapshot::getDeptId,deptId)
//                .in(RuleRecordReuseSnapshot::getParentCardId,modelIdList)
                .eq(RuleRecordReuseSnapshot::getDataStatus,0);
        ruleRecordReuseSnapshotService.remove(eqS3);
        List<RuleRecordReuse> scoreCardReuses = ruleRecordReuseMapper.selectList(eq3);
        List<RuleRecordReuseSnapshot> ruleRecordReuseSnapshots = new ArrayList<>();
        ruleRecordReuseSnapshots = JSON.parseArray(JSON.toJSONString(scoreCardReuses), RuleRecordReuseSnapshot.class);
        if (scoreCardReuses != null) {
            ruleRecordReuseSnapshotService.saveBatch(ruleRecordReuseSnapshots);
        }

    }
    /**
     * deptId为101时既为标准模型发布  需在原表中把101部门下四张表的数据进行每个部门的复制
     * @param modelIdList
     * @param deptIdStandard
     * @param deptId
     */
//    public void dataMigrationStandard(List<Integer> modelIdList,Integer deptIdStandard,Integer deptId){
//        //1.模型表
//        //先判断标准模型的modelIdList 原表中的此部门下是否存在 存在即先进行删除
//        List<RdeModelAntiFraud> rdeModelAntiFraudsDeptId = rdeModelAntiFraudMapper.queryByDeptId(deptId);
//        List<String> modifyModelIdList = new ArrayList<>();
//        if (CollUtil.isNotEmpty(rdeModelAntiFraudsDeptId)){
////            rdeModelAntiFraudMapper.deleteList(modelIdList,deptId);
//            // 找出原本状态为0的模型id，后面统一进行状态修改
//            for (RdeModelAntiFraud rdeModelAntiFraud : rdeModelAntiFraudsDeptId) {
//                if (rdeModelAntiFraud.getStatus().equals("0")){
//                    modifyModelIdList.add(rdeModelAntiFraud.getName());
//                }
//            }
//        }
//        modifyModelIdList = modifyModelIdList.stream().distinct().collect(Collectors.toList());
//        // 删除当前部门id下的所有标准模型
//        Example rdeModelAntiFraudExample = new Example(RdeModelAntiFraud.class);
//        Example.Criteria rdeModelAntiFraudCriteria = rdeModelAntiFraudExample.createCriteria();
//        rdeModelAntiFraudCriteria.andEqualTo("deptId", deptId).andEqualTo("deptFlag", "1");
//        rdeModelAntiFraudMapper.deleteByExample(rdeModelAntiFraudExample);
//
//        //查出原表中的标准模型 101部门下
//        List<RdeModelAntiFraud> rdeModelAntiFrauds = rdeModelAntiFraudMapper.queryList(modelIdList,deptIdStandard);
//        //将标准模型数据改变deptId编号再次存入原表中
//        List<RdeModelAntiFraud> rdeModelAntiFraudList = rdeModelAntiFrauds.stream().map(x -> {
//            x.setDeptId(deptId);
//            return x;
//        }).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(rdeModelAntiFraudList)){
//            rdeModelAntiFraudMapper.insertList(rdeModelAntiFraudList);
//        }
//        if (modifyModelIdList.size()!=0){
//            rdeModelAntiFraudMapper.updateList(modifyModelIdList,deptId);
//        }
//        //是否需要将其发布 即数据存入快照表中
////        List<RdeModelAntiFraudSnapshot> rdeModelAntiFraudSnapshots = new ArrayList<>();
////        rdeModelAntiFraudSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudList),RdeModelAntiFraudSnapshot.class);
////        if (CollUtil.isNotEmpty(rdeModelAntiFraudSnapshots)){
////            rdeModelAntiFraudSnapshotMapper.insertList(rdeModelAntiFraudSnapshots);
////        }
//        //2.策略组表
//        //先判断标准模型的modelIdList 原表中的此部门下是否存在 存在即先进行删除
//        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupsDeptId = rdeModelAntiFraudRuleGroupMapper.queryByDeptId(deptId);
//        List<String> modifyGroupIdList = new ArrayList<>();
//        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleGroupsDeptId)){
////            rdeModelAntiFraudRuleGroupMapper.deleteList(modelIdList,deptId);
//            for (RdeModelAntiFraudRuleGroup rdeModelAntiFraudRuleGroup : rdeModelAntiFraudRuleGroupsDeptId) {
//                if (rdeModelAntiFraudRuleGroup.getStatus().equals("0")){
//                    modifyGroupIdList.add(rdeModelAntiFraudRuleGroup.getName());
//                }
//            }
//        }
//        modifyGroupIdList = modifyGroupIdList.stream().distinct().collect(Collectors.toList());
//        // 删除当前部门id下的所有标准模型
//        Example rdeModelAntiFraudRuleGroupsExample = new Example(RdeModelAntiFraudRuleGroup.class);
//        Example.Criteria rdeModelAntiFraudRuleGroupsCriteria = rdeModelAntiFraudRuleGroupsExample.createCriteria();
//        rdeModelAntiFraudRuleGroupsCriteria.andEqualTo("deptId", deptId).andEqualTo("deptFlag", "1");
//        rdeModelAntiFraudRuleGroupMapper.deleteByExample(rdeModelAntiFraudRuleGroupsExample);
//
//        //查出原表中的标准模型包含的规则组 101部门下
//        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.queryList(modelIdList,deptIdStandard);
//        //将标准模型数据改变deptId编号再次存入原表中
//        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupsCopy = new ArrayList<>();
//        rdeModelAntiFraudRuleGroups.forEach(o->{
//            try {
//                rdeModelAntiFraudRuleGroupsCopy.add(o.clone());
//            }catch (CloneNotSupportedException e){
//                e.printStackTrace();
//            }
//        });
//        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupsCopy.stream().map(x -> {
//            x.setDeptId(deptId);
//            return x;
//        }).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleGroupList)){
//            rdeModelAntiFraudRuleGroupMapper.insertList(rdeModelAntiFraudRuleGroupList);
//        }
//        if (modifyGroupIdList.size()!=0){
//            rdeModelAntiFraudRuleGroupMapper.updateList(modifyGroupIdList,deptId);
//        }
//        //是否需要将其发布 即数据存入快照表中
////        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupSnapshots = new ArrayList<>();
////        rdeModelAntiFraudRuleGroupSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleGroupList),RdeModelAntiFraudRuleGroupSnapshot.class);
////        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleGroupSnapshots)){
////            rdeModelAntiFraudRuleGroupSnapshotMapper.insertList(rdeModelAntiFraudRuleGroupSnapshots);
////        }
//        //3.策略表
//        //先判断标准模型的modelIdList 原表中的此部门下是否存在 存在即先进行删除
//        List<Integer> groupIdList = rdeModelAntiFraudRuleGroups.stream().map(RdeModelAntiFraudRuleGroup::getId).collect(Collectors.toList());
//        List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecordsDeptId = rdeModelAntiFraudRuleRecordMapper.queryByDeptId(deptId);
//        List<String> modifyRecordIdList = new ArrayList<>();
//        if (CollUtil.isNotEmpty(rdeModelRuleRecordsDeptId)){
////            rdeModelAntiFraudRuleRecordMapper.deleteList(groupIdList,deptId);
//            for (RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord : rdeModelRuleRecordsDeptId) {
//                if (rdeModelAntiFraudRuleRecord.getStatus().equals("0")){
//                    modifyRecordIdList.add(rdeModelAntiFraudRuleRecord.getName());
//                }
//            }
//        }
//        modifyRecordIdList = modifyRecordIdList.stream().distinct().collect(Collectors.toList());
//        // 删除当前部门下的标准模型
//        Example rdeModelRuleRecordsExample = new Example(RdeModelAntiFraudRuleRecord.class);
//        Example.Criteria rdeModelRuleRecordsCriteria = rdeModelRuleRecordsExample.createCriteria();
//        rdeModelRuleRecordsCriteria.andEqualTo("deptId", deptId).andEqualTo("deptFlag", "1");
//        rdeModelAntiFraudRuleRecordMapper.deleteByExample(rdeModelRuleRecordsExample);
//
//        //查出原表中的标准模型包含的规则 101部门下
//        List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecords = rdeModelAntiFraudRuleRecordMapper.queryList(groupIdList,deptIdStandard);
//        List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecordsCopy = new ArrayList<>();
//        rdeModelRuleRecords.forEach(o->{
//            try {
//                rdeModelRuleRecordsCopy.add(o.clone());
//            }catch (CloneNotSupportedException e){
//                e.printStackTrace();
//            }
//        });
//        //将标准模型数据改变deptId编号再次存入原表中
//        List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = rdeModelRuleRecordsCopy.stream().map(x -> {
//            x.setDeptId(deptId);
//            return x;
//        }).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleRecordList)){
//            rdeModelAntiFraudRuleRecordMapper.insertList(rdeModelAntiFraudRuleRecordList);
//        }
//        if (modifyRecordIdList.size()!=0){
//            rdeModelAntiFraudRuleRecordMapper.updateList(modifyRecordIdList,deptId);
//        }
//        //是否需要将其发布 即数据存入快照表中
////        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudRuleRecordSnapshots = new ArrayList<>();
////        rdeModelAntiFraudRuleRecordSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelAntiFraudRuleRecordList),RdeModelAntiFraudRuleRecordSnapshot.class);
////        if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleRecordSnapshots)){
////            rdeModelAntiFraudRuleRecordSnapshotMapper.insertList(rdeModelAntiFraudRuleRecordSnapshots);
////        }
//
//        //4.规则表
//        //先判断标准模型的modelIdList 原表中的此部门下是否存在 存在即先进行删除
//        List<Integer> codeIdList = rdeModelRuleRecords.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList());
////        List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevelsDeptId = rdeModelDecisionCodeLevelMapper.queryList(codeIdList,deptId);
////        if (CollUtil.isNotEmpty(rdeModelDecisionCodeLevelsDeptId)){
////            rdeModelDecisionCodeLevelMapper.deleteList(codeIdList,deptId);
////        }
//
//        // 删除当前部门下的标准模型
//        Example rdeModelDecisionCodeLevelsExample = new Example(RdeModelDecisionCodeLevel.class);
//        Example.Criteria rdeModelDecisionCodeLevelsCriteria = rdeModelDecisionCodeLevelsExample.createCriteria();
//        rdeModelDecisionCodeLevelsCriteria.andEqualTo("deptId", deptId).andEqualTo("deptFlag", "1");
//        rdeModelDecisionCodeLevelMapper.deleteByExample(rdeModelDecisionCodeLevelsExample);
//
//        //查出原表中的标准模型包含的规则 101部门下
//        List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevels = rdeModelDecisionCodeLevelMapper.queryList(codeIdList,deptIdStandard);
//        //将标准模型数据改变deptId编号再次存入原表中
//        List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevelList = rdeModelDecisionCodeLevels.stream().map(x -> {
//            x.setDeptId(deptId);
//            return x;
//        }).collect(Collectors.toList());
//        if (CollUtil.isNotEmpty(rdeModelDecisionCodeLevelList)){
//            rdeModelDecisionCodeLevelMapper.insertList(rdeModelDecisionCodeLevelList);
//        }
//        //是否需要将其发布 即数据存入快照表中
////        List<RdeModelDecisionCodeLevelSnapshot> rdeModelDecisionCodeLevelSnapshots = new ArrayList<>();
////        rdeModelDecisionCodeLevelSnapshots = JSON.parseArray(JSON.toJSONString(rdeModelDecisionCodeLevels),RdeModelDecisionCodeLevelSnapshot.class);
////        if (CollUtil.isNotEmpty(rdeModelDecisionCodeLevelSnapshots)){
////            rdeModelDecisionCodeLevelSnapshotMapper.insertList(rdeModelDecisionCodeLevelSnapshots);
////        }
//    }

    public void dataMigrationStandard2(List<Integer> modelIdList,Integer deptIdStandard,Integer deptId,String projectCode,String ruleCode){
        List<Integer> modelIdUsing = new ArrayList<>();
        for (int i = 0; i < modelIdList.size(); i++) {
            Integer modelid = modelIdList.get(i);
            //1.模型表
            //查出原表中的标准模型 101部门下
            RdeModelAntiFraud rdeModelAntiFraud = rdeModelAntiFraudMapper.queryById(modelid,deptIdStandard,projectCode);
            //先判断标准模型的modelName 原表中的此部门下是否存在 存在即先进行删除
            String modelName = rdeModelAntiFraud.getName()==null ? "" : rdeModelAntiFraud.getName();
            RdeModelAntiFraud rdeModelAntiFraudCurrDept = rdeModelAntiFraudMapper.queryByDeptId(modelName,deptId,projectCode);
            if (!Objects.isNull(rdeModelAntiFraudCurrDept)){
                rdeModelAntiFraudMapper.deleteById(rdeModelAntiFraudCurrDept.getId());
                rdeModelAntiFraud.setStatus(rdeModelAntiFraudCurrDept.getStatus());
            }

            //将标准模型数据改变deptId编号再次存入原表中
            rdeModelAntiFraud.setDeptId(deptId);
            rdeModelAntiFraud.setId(null);
            if (!Objects.isNull(rdeModelAntiFraud)){
                rdeModelAntiFraudMapper.insert(rdeModelAntiFraud);
            }

            //插入完成后 查询插入后的modelId
            Integer currModelId = rdeModelAntiFraudMapper.queryByDeptId(modelName, deptId,projectCode).getId();
            modelIdUsing.add(currModelId);
            //2.策略组表
            //查出原表中的标准模型包含的规则组 101部门下
            List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.queryListByModelId(modelid,deptIdStandard,projectCode);
            //查询原来当前部门下的策略组进行删除
            Set<String> modifyGroupIdList = new HashSet<>();
            List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupsCurrDept = null;
            if (rdeModelAntiFraudCurrDept!=null){
                rdeModelAntiFraudRuleGroupsCurrDept = rdeModelAntiFraudRuleGroupMapper.queryByOldModelId(rdeModelAntiFraudCurrDept.getId(),deptId,projectCode);
                //查出需要进行状态修改的策略组名称
                if (rdeModelAntiFraudRuleGroupsCurrDept.size()>0){
                    for (int j = 0; j < rdeModelAntiFraudRuleGroupsCurrDept.size(); j++) {
                        RdeModelAntiFraudRuleGroup rdeModelAntiFraudRuleGroupCurrDept = rdeModelAntiFraudRuleGroupsCurrDept.get(j);
                        if (rdeModelAntiFraudRuleGroupCurrDept.getStatus().equals("0")){
                            modifyGroupIdList.add(rdeModelAntiFraudRuleGroupCurrDept.getName());
                        }
                        rdeModelAntiFraudRuleGroupMapper.deleteById(rdeModelAntiFraudRuleGroupCurrDept.getId());
                    }
                }
            }
            List<String> modifyGroupCollect = new ArrayList<>(modifyGroupIdList);
            List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupsCopy = new ArrayList<>();
            rdeModelAntiFraudRuleGroups.forEach(o->{
                try {
                    rdeModelAntiFraudRuleGroupsCopy.add(o.clone());
                }catch (CloneNotSupportedException e){
                    e.printStackTrace();
                }
            });
            List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupsCopy.stream().map(x -> {
                x.setDeptId(deptId);
                x.setId(null);
                x.setModelId(currModelId);
                return x;
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleGroupList)){
                for (RdeModelAntiFraudRuleGroup rdeModelAntiFraudRuleGroup : rdeModelAntiFraudRuleGroupList) {
                    rdeModelAntiFraudRuleGroupMapper.insert(rdeModelAntiFraudRuleGroup);
                }

            }
            if (modifyGroupCollect.size()!=0){
                rdeModelAntiFraudRuleGroupMapper.updateList(modifyGroupCollect,deptId);
            }

            List<RdeModelAntiFraudRuleGroup> newRdeModelAntiFraudRuleGroups = rdeModelAntiFraudRuleGroupMapper.queryListByModelId(currModelId, deptId,projectCode);

            //3.策略表
            //查出原表中的标准模型包含的规则 101部门下
            for (int l = 0; l < rdeModelAntiFraudRuleGroups.size(); l++) {
                RdeModelAntiFraudRuleGroup rdeModelAntiFraudRuleGroup = rdeModelAntiFraudRuleGroups.get(l);
                // 查询标准策略组下的策略集
                List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecords = rdeModelAntiFraudRuleRecordMapper.queryListByGroupId(rdeModelAntiFraudRuleGroup.getId(),deptIdStandard,projectCode);
                Set<String> modifyRecordIdList = new HashSet<>();
                Set<String> modifyCodeIdList = new HashSet<>();
                if (rdeModelAntiFraudRuleGroupsCurrDept!=null){
                    for (int j = 0; j < rdeModelAntiFraudRuleGroupsCurrDept.size(); j++) {
                        RdeModelAntiFraudRuleGroup group = rdeModelAntiFraudRuleGroupsCurrDept.get(j);
                        if (group.getName().equals(rdeModelAntiFraudRuleGroup.getName())){
                            // 查询发布模型下当前部门下的策略组下的源策略集
                            List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecords = rdeModelAntiFraudRuleRecordMapper.queryByGroupId(group.getId(),deptId,projectCode);
                            for (RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord : rdeModelAntiFraudRuleRecords) {
                                if (rdeModelAntiFraudRuleRecord.getStatus().equals("0")){
                                    modifyRecordIdList.add(rdeModelAntiFraudRuleRecord.getName());
                                }
                                rdeModelAntiFraudRuleRecordMapper.deleteById(rdeModelAntiFraudRuleRecord.getId());
                            }
                            // 删除原来策略集对应的规则表
                            List<Integer> oldCodeIdList = rdeModelAntiFraudRuleRecords.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList());
                            List<RdeModelDecisionCodeLevel> oldRdeModelDecisionCodeLevels = rdeModelDecisionCodeLevelMapper.queryList(oldCodeIdList,deptId,projectCode,ruleCode);
                            for (RdeModelDecisionCodeLevel oldRdeModelDecisionCodeLevel : oldRdeModelDecisionCodeLevels) {
                                if (oldRdeModelDecisionCodeLevel.getStatus().equals("0")){
                                    modifyCodeIdList.add(oldRdeModelDecisionCodeLevel.getStatus());
                                }
                                rdeModelDecisionCodeLevelMapper.deleteById(oldRdeModelDecisionCodeLevel.getId());
                            }
                        }
                    }
                }
                List<String> modifyRecordCollect = new ArrayList<>(modifyRecordIdList);
                List<RdeModelAntiFraudRuleRecord> rdeModelRuleRecordsCopy = new ArrayList<>();
                rdeModelRuleRecords.forEach(o->{
                    try {
                        rdeModelRuleRecordsCopy.add(o.clone());
                    }catch (CloneNotSupportedException e){
                        e.printStackTrace();
                    }
                });
                //查找当前标准策略组下当前部门的策略组id
                Integer currGroupId = null;
                for (RdeModelAntiFraudRuleGroup newRdeModelAntiFraudRuleGroup : newRdeModelAntiFraudRuleGroups) {
                    if (newRdeModelAntiFraudRuleGroup.getName().equals(rdeModelAntiFraudRuleGroup.getName())){
                        currGroupId = newRdeModelAntiFraudRuleGroup.getId();
                    }
                }
                //将标准模型数据改变deptId编号再次存入原表中
                Integer finalCurrGroupId = currGroupId;
                List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = rdeModelRuleRecordsCopy.stream().map(x -> {
                    x.setDeptId(deptId);
                    x.setModelId(currModelId);
                    x.setId(null);
                    x.setGroupId(finalCurrGroupId);
                    return x;
                }).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(rdeModelAntiFraudRuleRecordList)){
                    for (RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord : rdeModelAntiFraudRuleRecordList) {
                        rdeModelAntiFraudRuleRecordMapper.insert(rdeModelAntiFraudRuleRecord);
                    }

                }
                if (modifyRecordIdList.size()!=0){
                    rdeModelAntiFraudRuleRecordMapper.updateList(modifyRecordCollect,deptId);
                }

                List<RdeModelAntiFraudRuleRecord> newRdeModelAntiFraudRuleRecords = rdeModelAntiFraudRuleRecordMapper.queryListByGroupId(finalCurrGroupId, deptId,projectCode);

                //4.规则表
                //查出原表中的当前策略组下策略集包含的规则 101部门下
                List<Integer> codeIdList = rdeModelRuleRecords.stream().map(RdeModelAntiFraudRuleRecord::getCodeId).collect(Collectors.toList());
                //查出原表中的标准模型包含的规则 101部门下
                List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevels = rdeModelDecisionCodeLevelMapper.queryList(codeIdList,deptIdStandard,projectCode,ruleCode);
                //将标准模型数据改变deptId编号再次存入原表中
                List<RdeModelDecisionCodeLevel> rdeModelDecisionCodeLevelList = rdeModelDecisionCodeLevels.stream().map(x -> {
                    x.setDeptId(deptId);
                    x.setId(null);
                    return x;
                }).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(rdeModelDecisionCodeLevelList)){
                    for (RdeModelDecisionCodeLevel rdeModelDecisionCodeLevel : rdeModelDecisionCodeLevelList) {
                        rdeModelDecisionCodeLevelMapper.insert(rdeModelDecisionCodeLevel);
                    }

                }
                List<String> codeCollect = newRdeModelAntiFraudRuleRecords.stream().map(RdeModelAntiFraudRuleRecord::getCode).collect(Collectors.toList());
                List<RdeModelDecisionCodeLevel> newRdeModelDecisionCodeLevels = rdeModelDecisionCodeLevelMapper.queryListByCode(codeCollect,deptId,projectCode,ruleCode);
                for (int i1 = 0; i1 < newRdeModelAntiFraudRuleRecords.size(); i1++) {
                    for (int i2 = 0; i2 < newRdeModelDecisionCodeLevels.size(); i2++) {
                        RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord = newRdeModelAntiFraudRuleRecords.get(i1);
                        RdeModelDecisionCodeLevel rdeModelDecisionCodeLevel = newRdeModelDecisionCodeLevels.get(i2);
                        if (rdeModelAntiFraudRuleRecord.getCode().equals(rdeModelDecisionCodeLevel.getCode())){
                            newRdeModelAntiFraudRuleRecords.get(i1).setCodeId(rdeModelDecisionCodeLevel.getId());
                            rdeModelAntiFraudRuleRecordMapper.updateById(newRdeModelAntiFraudRuleRecords.get(i1));
                        }
                    }
                }

            }
        }
        List<Integer> modelIdDeleted = rdeModelAntiFraudMapper.selectDeleted(modelIdUsing, deptId,projectCode,ruleCode);
        if(modelIdDeleted!=null && modelIdDeleted.size()>0){
            rdeModelAntiFraudMapper.deleteList(modelIdDeleted,deptId,projectCode,ruleCode);
            rdeModelAntiFraudRuleGroupMapper.deleteList(modelIdDeleted,deptId,projectCode,ruleCode);
            List<Integer> groupIdDeleted = rdeModelAntiFraudRuleGroupMapper.selectDeleted(modelIdDeleted, deptId,projectCode,ruleCode);
            if (groupIdDeleted!=null && groupIdDeleted.size()>0){
                rdeModelAntiFraudRuleRecordMapper.deleteList(groupIdDeleted,deptId,projectCode,ruleCode);
                List<Integer> codeIdDeleted = rdeModelAntiFraudRuleRecordMapper.getCodeIdDeleted(groupIdDeleted,deptId,projectCode,ruleCode);
                if (codeIdDeleted!=null && codeIdDeleted.size()>0){
                    rdeModelDecisionCodeLevelMapper.deleteList(codeIdDeleted,deptId,projectCode,ruleCode);
                }
            }
        }
    }

}
