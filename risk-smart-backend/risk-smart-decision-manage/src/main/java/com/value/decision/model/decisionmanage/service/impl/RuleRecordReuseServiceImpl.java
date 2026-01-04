package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.ScoreCardReuse;
import com.value.decision.model.decisionmanage.model.dto.model.RuleRecordReuseVO;
import com.value.decision.model.decisionmanage.service.IRuleRecordReuseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.version.domain.RdeModelAntiFraudRuleGroupVersion;
import com.value.decision.version.domain.RdeModelAntiFraudRuleRecordVersion;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleGroupVersionMapper;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleRecordVersionMapper;
import com.value.decision.version.mapper.RdeModelAntiFraudVersionMapper;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 评分卡复用关联表 服务实现类
 * </p>
 *
 * @author dianne
 * @since 2024-12-10
 */
@Service
public class RuleRecordReuseServiceImpl extends ServiceImpl<RuleRecordReuseMapper, RuleRecordReuse> implements IRuleRecordReuseService {

    @Autowired
    private RdeModelAntiFraudVersionMapper rdeModelAntiFraudVersionMapper;
    @Autowired
    private RdeModelAntiFraudRuleGroupVersionMapper rdeModelAntiFraudRuleGroupVersionMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordVersionMapper rdeModelAntiFraudRuleRecordVersionMapper;

    @Override
    @Transactional
    public void addStandardModel(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser) {
        //将标准策略信息与当前用户信息绑定存入关联表
        if (CollectionUtils.isNotEmpty(ruleRecordReuseVO.getRuleRecordData())){
            //查询出已关联的数据
            LambdaQueryWrapper<RuleRecordReuse> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId, RuleConstants.POLICY_GROUP_LIST)
                    .eq(RuleRecordReuse::getDataStatus,0);
            List<RuleRecordReuse> ruleRecordList = this.list(wrapper);
            List<Integer> ruleIdList = ruleRecordList.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
            this.removeByIds(ruleIdList);

            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
            ruleRecordReuseVO.getRuleRecordData().stream().forEach(x ->{
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
                ruleRecordReuse.setDataStatus(0);
                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                ruleRecordReuse.setMoudleId(RuleConstants.POLICY_GROUP_LIST);
                ruleRecordReuse.setParentCardId(x.getId());
                ruleRecordReuse.setVersionControl(x.getVersionControl());
                ruleRecordReuseList.add(ruleRecordReuse);
            });
            this.saveBatch(ruleRecordReuseList);

//            List<RdeModelAntiFraud> collect = ruleRecordReuseVO.getRuleRecordData().stream().filter(x -> !ruleIdList.contains(x.getId())).collect(Collectors.toList());
//
//            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
//            collect.stream().forEach(x ->{
//                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
//                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
//                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
//                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
//                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
//                ruleRecordReuse.setDataStatus(0);
//                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
//                ruleRecordReuse.setMoudleId(RuleConstants.POLICY_GROUP_LIST);
//                ruleRecordReuse.setParentCardId(x.getId());
//                ruleRecordReuse.setVersionControl(x.getVersionControl());
//                ruleRecordReuseList.add(ruleRecordReuse);
//            });
//            this.saveBatch(ruleRecordReuseList);
//
//            //对比数据库已经移除的
//            List<Integer> collect1 = ruleRecordReuseVO.getRuleRecordData().stream().map(RdeModelAntiFraud::getId).collect(Collectors.toList());
//            List<RuleRecordReuse> collect2 = ruleRecordList.stream().filter(x -> !collect1.contains(x.getParentCardId())).collect(Collectors.toList());
//            List<Integer> collect3 = collect2.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
//            this.removeByIds(collect3);

        }else {
            //传入引用标准的为空时移除所有关联的id
            LambdaQueryWrapper<RuleRecordReuse> wrapperDel = Wrappers.lambdaQuery();
            wrapperDel.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId,RuleConstants.POLICY_GROUP_LIST);
            this.remove(wrapperDel);
        }

    }

    @Override
    @Transactional
    public void addStandardRuleGroup(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser) {
        //将标准规则组信息与当前用户信息绑定存入关联表
        if (CollectionUtils.isNotEmpty(ruleRecordReuseVO.getRuleRecordRuleGroupData())){
            //查询出已关联的数据
            LambdaQueryWrapper<RuleRecordReuse> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_GROUP_LIST)
                    .eq(RuleRecordReuse::getDataStatus,0);
            List<RuleRecordReuse> ruleRecordList = this.list(wrapper);
            List<Integer> ruleIdList = ruleRecordList.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
            this.removeByIds(ruleIdList);

            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
            ruleRecordReuseVO.getRuleRecordRuleGroupData().stream().forEach(x ->{
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
                ruleRecordReuse.setDataStatus(0);
                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                ruleRecordReuse.setMoudleId(RuleConstants.RULE_GROUP_LIST);
                ruleRecordReuse.setParentCardId(x.getId());
                ruleRecordReuse.setRuleId(ruleRecordReuseVO.getRuleId());
                ruleRecordReuse.setVersionControl(x.getVersionControl());
                ruleRecordReuseList.add(ruleRecordReuse);
            });
            this.saveBatch(ruleRecordReuseList);

//            List<RdeModelAntiFraudRuleGroup> collect = ruleRecordReuseVO.getRuleRecordRuleGroupData().stream().filter(x -> !ruleIdList.contains(x.getId())).collect(Collectors.toList());
//
//            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
//            collect.stream().forEach(x ->{
//                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
//                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
//                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
//                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
//                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
//                ruleRecordReuse.setDataStatus(0);
//                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
//                ruleRecordReuse.setMoudleId(RuleConstants.RULE_GROUP_LIST);
//                ruleRecordReuse.setParentCardId(x.getId());
//                ruleRecordReuse.setRuleId(ruleRecordReuseVO.getRuleId());
//                ruleRecordReuse.setVersionControl(x.getVersionControl());
//                ruleRecordReuseList.add(ruleRecordReuse);
//            });
//            this.saveBatch(ruleRecordReuseList);
//
//            //对比数据库已经移除的
//            List<Integer> collect1 = ruleRecordReuseVO.getRuleRecordRuleGroupData().stream().map(RdeModelAntiFraudRuleGroup::getId).collect(Collectors.toList());
//            List<RuleRecordReuse> collect2 = ruleRecordList.stream().filter(x -> !collect1.contains(x.getParentCardId())).collect(Collectors.toList());
//            List<Integer> collect3 = collect2.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
//            this.removeByIds(collect3);
        }else {
            //传入引用标准的为空时移除所有关联的id
            LambdaQueryWrapper<RuleRecordReuse> wrapperDel = Wrappers.lambdaQuery();
            wrapperDel.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId,RuleConstants.RULE_GROUP_LIST);
            this.remove(wrapperDel);
        }
    }


    @Override
    @Transactional
    public void addStandardRuleRecord(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser) {
        //将标准规则组信息与当前用户信息绑定存入关联表
        if (CollectionUtils.isNotEmpty(ruleRecordReuseVO.getRuleRecordRuleRecordData())){
            //查询出已关联的数据
            LambdaQueryWrapper<RuleRecordReuse> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_LIST)
                    .eq(RuleRecordReuse::getDataStatus,0);
            List<RuleRecordReuse> ruleRecordList = this.list(wrapper);
            List<Integer> ruleIdList = ruleRecordList.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
            this.removeByIds(ruleIdList);

            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
            ruleRecordReuseVO.getRuleRecordRuleRecordData().stream().forEach(x ->{
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
                ruleRecordReuse.setDataStatus(0);
                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                ruleRecordReuse.setMoudleId(RuleConstants.RULE_LIST);
                ruleRecordReuse.setParentCardId(x.getId());
                ruleRecordReuse.setGroupId(ruleRecordReuseVO.getGroupId());
                ruleRecordReuse.setRuleId(ruleRecordReuseVO.getRuleId());
                ruleRecordReuse.setVersionControl(x.getVersionControl());
                ruleRecordReuseList.add(ruleRecordReuse);
            });
            this.saveBatch(ruleRecordReuseList);

//            List<RdeModelAntiFraudRuleRecord> collect = ruleRecordReuseVO.getRuleRecordRuleRecordData().stream().filter(x -> !ruleIdList.contains(x.getId())).collect(Collectors.toList());
//
//            List<RuleRecordReuse> ruleRecordReuseList = new ArrayList<>();
//            collect.stream().forEach(x ->{
//                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
//                ruleRecordReuse.setBuildProjectCode(ruleRecordReuseVO.getBuildProjectCode());
//                ruleRecordReuse.setBuildBusinessCode(ruleRecordReuseVO.getBuildBusinessCode());
//                ruleRecordReuse.setBuildRuleCode(ruleRecordReuseVO.getBuildRuleCode());
//                ruleRecordReuse.setButtonState(RuleConstants.DATA_ENABLE);
//                ruleRecordReuse.setDataStatus(0);
//                ruleRecordReuse.setDeptId(loginUser.getSysUser().getDeptId().intValue());
//                ruleRecordReuse.setMoudleId(RuleConstants.RULE_LIST);
//                ruleRecordReuse.setParentCardId(x.getId());
//                ruleRecordReuse.setGroupId(ruleRecordReuseVO.getGroupId());
//                ruleRecordReuse.setRuleId(ruleRecordReuseVO.getRuleId());
//                ruleRecordReuse.setVersionControl(x.getVersionControl());
//                ruleRecordReuseList.add(ruleRecordReuse);
//            });
//            this.saveBatch(ruleRecordReuseList);
//
//            //对比数据库已经移除的
//            List<Integer> collect1 = ruleRecordReuseVO.getRuleRecordRuleRecordData().stream().map(RdeModelAntiFraudRuleRecord::getId).collect(Collectors.toList());
//            List<RuleRecordReuse> collect2 = ruleRecordList.stream().filter(x -> !collect1.contains(x.getParentCardId())).collect(Collectors.toList());
//            List<Integer> collect3 = collect2.stream().map(RuleRecordReuse::getId).collect(Collectors.toList());
//            this.removeByIds(collect3);
        }else {
            //传入引用标准的为空时移除所有关联的id
            LambdaQueryWrapper<RuleRecordReuse> wrapperDel = Wrappers.lambdaQuery();
            wrapperDel.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                    .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                    .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                    .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                    .eq(RuleRecordReuse::getMoudleId,RuleConstants.RULE_LIST);
            this.remove(wrapperDel);
        }
    }

    @Override
    public AjaxResult selectStandardModel(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser) {
        switch (ruleRecordReuseVO.getMoudleId()){
            case 1:
                LambdaQueryWrapper<RuleRecordReuse> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                        .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                        .eq(RuleRecordReuse::getMoudleId,RuleConstants.POLICY_GROUP_LIST)
                        .eq(RuleRecordReuse::getDataStatus,0);
                List<RuleRecordReuse> cardReuseList = this.list(wrapper);
                List<RdeModelAntiFraudVersion> ruleRecordReuseList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cardReuseList)){
                    //策略版本表
                    cardReuseList.stream().forEach(x ->{
                        LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapper1 = Wrappers.lambdaQuery();
                        wrapper1.eq(RdeModelAntiFraudVersion::getId,x.getParentCardId())
                                .eq(RdeModelAntiFraudVersion::getVersionControl,x.getVersionControl())
                                .eq(RdeModelAntiFraudVersion::getDataStatus,0);
                        RdeModelAntiFraudVersion rdeModelAntiFraudVersion = rdeModelAntiFraudVersionMapper.selectOne(wrapper1);
                        ruleRecordReuseList.add(rdeModelAntiFraudVersion);
                    });
                }
                return AjaxResult.success(ruleRecordReuseList);
            case 2:
                LambdaQueryWrapper<RuleRecordReuse> wrapperGroup = Wrappers.lambdaQuery();
                wrapperGroup.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                        .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                        .eq(RuleRecordReuse::getRuleId,ruleRecordReuseVO.getRuleId())
                        .eq(RuleRecordReuse::getMoudleId,RuleConstants.RULE_GROUP_LIST)
                        .eq(RuleRecordReuse::getDataStatus,0);
                List<RuleRecordReuse> cardReuseGroupList = this.list(wrapperGroup);
                List<RdeModelAntiFraudRuleGroupVersion> ruleRecordReuseGroupList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cardReuseGroupList)){
                    //规则组版本表
                    cardReuseGroupList.stream().forEach(x ->{
                        LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperGroup1 = Wrappers.lambdaQuery();
                        wrapperGroup1.eq(RdeModelAntiFraudRuleGroupVersion::getId,x.getParentCardId())
                                .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,x.getVersionControl())
                                .eq(RdeModelAntiFraudRuleGroupVersion::getDataStatus,0);
                        RdeModelAntiFraudRuleGroupVersion rdeModelAntiFraudRuleGroupVersion = rdeModelAntiFraudRuleGroupVersionMapper.selectOne(wrapperGroup1);
                        ruleRecordReuseGroupList.add(rdeModelAntiFraudRuleGroupVersion);
                    });
                }
                return AjaxResult.success(ruleRecordReuseGroupList);
            case 3:
                LambdaQueryWrapper<RuleRecordReuse> wrapperRule = Wrappers.lambdaQuery();
                wrapperRule.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                        .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                        .eq(RuleRecordReuse::getRuleId,ruleRecordReuseVO.getRuleId())
                        .eq(RuleRecordReuse::getGroupId,ruleRecordReuseVO.getGroupId())
                        .eq(RuleRecordReuse::getMoudleId,RuleConstants.RULE_LIST)
                        .eq(RuleRecordReuse::getDataStatus,0);
                List<RuleRecordReuse> cardReuseRuleList = this.list(wrapperRule);
                List<RdeModelAntiFraudRuleRecordVersion> ruleRecordReuseRuleList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(cardReuseRuleList)){
                    //规则组版本表
                    cardReuseRuleList.stream().forEach(x ->{
                        LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperRule1 = Wrappers.lambdaQuery();
                        wrapperRule1.eq(RdeModelAntiFraudRuleRecordVersion::getId,x.getParentCardId())
                                .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,x.getVersionControl())
                                .eq(RdeModelAntiFraudRuleRecordVersion::getDataStatus,0);
                        RdeModelAntiFraudRuleRecordVersion rdeModelAntiFraudRuleRecordVersion = rdeModelAntiFraudRuleRecordVersionMapper.selectOne(wrapperRule1);
                        ruleRecordReuseRuleList.add(rdeModelAntiFraudRuleRecordVersion);
                    });
                }
                return AjaxResult.success(ruleRecordReuseRuleList);
        }

       return AjaxResult.success();
    }

    @Override
    @Transactional
    public void deletePolicyGroup(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser) {
          switch (ruleRecordReuseVO.getMoudleId()){
              case 1: //删除策略 即groupId ruleId为空
                  LambdaQueryWrapper<RuleRecordReuse> wrapper = Wrappers.lambdaQuery();
                  wrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                          .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                          .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                          .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                          .eq(RuleRecordReuse::getParentCardId,ruleRecordReuseVO.getParentCardId())
                          .eq(RuleRecordReuse::getVersionControl,ruleRecordReuseVO.getVersionControl())
                          .isNull(RuleRecordReuse::getGroupId)
                          .isNull(RuleRecordReuse::getRuleId);
                  this.remove(wrapper);
                  break;
              case 2: //删除规则组 即groupId为空 ruleId不为空
                  LambdaQueryWrapper<RuleRecordReuse> wrapperGroup = Wrappers.lambdaQuery();
                  wrapperGroup.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                          .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                          .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                          .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                          .eq(RuleRecordReuse::getVersionControl,ruleRecordReuseVO.getVersionControl())
                          .eq(RuleRecordReuse::getParentCardId,ruleRecordReuseVO.getParentCardId())
                          .eq(RuleRecordReuse::getRuleId,ruleRecordReuseVO.getRuleId())
                          .isNull(RuleRecordReuse::getGroupId);
                  this.remove(wrapperGroup);
                  break;
              case 3: //删除规则 即groupId不为空 ruleId不为空
                  LambdaQueryWrapper<RuleRecordReuse> wrapperRule = Wrappers.lambdaQuery();
                  wrapperRule.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                          .eq(RuleRecordReuse::getBuildProjectCode,ruleRecordReuseVO.getBuildProjectCode())
                          .eq(RuleRecordReuse::getBuildBusinessCode,ruleRecordReuseVO.getBuildBusinessCode())
                          .eq(RuleRecordReuse::getBuildRuleCode,ruleRecordReuseVO.getBuildRuleCode())
                          .eq(RuleRecordReuse::getParentCardId,ruleRecordReuseVO.getParentCardId())
                          .eq(RuleRecordReuse::getRuleId,ruleRecordReuseVO.getRuleId())
                          .eq(RuleRecordReuse::getGroupId,ruleRecordReuseVO.getGroupId())
                          .eq(RuleRecordReuse::getVersionControl,ruleRecordReuseVO.getVersionControl());
                  this.remove(wrapperRule);
                  break;
          }
    }
}
