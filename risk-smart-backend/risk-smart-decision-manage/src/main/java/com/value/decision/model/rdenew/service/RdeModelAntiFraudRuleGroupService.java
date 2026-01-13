package com.value.decision.model.rdenew.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.risksmart.common.core.utils.StringUtils;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudRuleGroupVO;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.model.rdenew.domain.*;
import com.value.decision.model.rdenew.mapper.*;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleGroupVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleGroupSnapshotMapper;
import com.value.decision.version.domain.RdeModelAntiFraudRuleGroupVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleGroupVersionMapper;
import com.value.decision.common.security.LoginUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RdeModelAntiFraudRuleGroupService {
    @Autowired
    private RdeModelAntiFraudRuleGroupMapper mapper;
    @Autowired
    private RdeModelAntiFraudRuleGroupVersionMapper versionMapper;
    @Autowired
    private RdeModelAntiFraudRuleGroupSnapshotMapper snapshotMapper;
    @Autowired
    private RdeModelDecisionCodeLevelMapper codeLevelMapper;
    @Autowired
    private RdeModelDecisionRuleRecordMapper codeRuleMapper;
    @Autowired
    private RdeModelRuleActivityRecordMapper activityRecordMapper;
    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;
    @Autowired
    private ProcessPolicyReferenceService processPolicyReferenceService;
    @Resource
    private RuoYiService ruoYiService;


    /**
     * 查询列表
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleGroup> newList(ModelAntiFraudRuleGroupVO query, HttpServletRequest request){
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LambdaQueryWrapper<RdeModelAntiFraudRuleGroup> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleGroup::getModelId,query.getModelId())
                .eq(RdeModelAntiFraudRuleGroup::getProjectCode,query.getProjectCode())
                .eq(RdeModelAntiFraudRuleGroup::getBusinessCode,query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleGroup::getRuleCode,query.getRuleCode())
                .eq(RdeModelAntiFraudRuleGroup::getModelId,query.getModelId())
//                .eq(RdeModelAntiFraudRuleGroup::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(RdeModelAntiFraudRuleGroup::getDataStatus,0);
        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = mapper.selectList(wrapper);
        //关联表查询是否有引用标准规则组
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,query.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,query.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,query.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getRuleId,query.getModelId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (CollectionUtils.isNotEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudRuleGroupVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getStatus,1);
                RdeModelAntiFraudRuleGroupVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraudRuleGroup scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraudRuleGroup.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudRuleGroupList.add(scoreCardRecordReuse);
            });
        }

        //搜索
        if (StringUtils.isNotEmpty(query.getName())){
            List<RdeModelAntiFraudRuleGroup> collect = rdeModelAntiFraudRuleGroupList.stream().filter(x -> x.getName().contains(query.getName())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudRuleGroupList;
    }


    /**
     * 查询自建列表
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleGroup> newBuildList(ModelAntiFraudRuleGroupVO query, HttpServletRequest request){
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LambdaQueryWrapper<RdeModelAntiFraudRuleGroup> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleGroup::getModelId,query.getModelId())
                .eq(RdeModelAntiFraudRuleGroup::getProjectCode,query.getProjectCode())
                .eq(RdeModelAntiFraudRuleGroup::getBusinessCode,query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleGroup::getRuleCode,query.getRuleCode())
                .eq(RdeModelAntiFraudRuleGroup::getModelId,query.getModelId())
                .eq(RdeModelAntiFraudRuleGroup::getDataStatus,0);
        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = mapper.selectList(wrapper);
        //关联表查询是否有引用标准规则组
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,query.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,query.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,query.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getRuleId,query.getModelId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (CollectionUtils.isNotEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleGroupVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudRuleGroupVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudRuleGroupVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudRuleGroupVersion::getStatus,1);
                RdeModelAntiFraudRuleGroupVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraudRuleGroup scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraudRuleGroup.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudRuleGroupList.add(scoreCardRecordReuse);
            });
        }


        if (CollectionUtils.isNotEmpty(query.getModelAntiGroupDataList())){
            rdeModelAntiFraudRuleGroupList.addAll(query.getModelAntiGroupDataList());
        }
        if (CollectionUtils.isNotEmpty(query.getModelAntiGroupDataDelList())){
            rdeModelAntiFraudRuleGroupList.removeAll(query.getModelAntiGroupDataDelList());
        }

        //搜索
        if (StringUtils.isNotEmpty(query.getName())){
            List<RdeModelAntiFraudRuleGroup> collect = rdeModelAntiFraudRuleGroupList.stream().filter(x -> x.getName().contains(query.getName())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudRuleGroupList;
    }


    /**
     * 查询标准列表
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleGroupSnapshot> standardList(ModelAntiFraudRuleGroupVO query, HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleGroupSnapshot::getProjectCode, query.getProjectCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getBusinessCode, query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getRuleCode, query.getRuleCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, query.getModelId())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus, 0)
                .orderByDesc(RdeModelAntiFraudRuleGroupSnapshot::getUpdateTime);
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupList = snapshotMapper.selectList(wrapper);

        // 查询当前用户的复用关联表数据
        LambdaQueryWrapper<RuleRecordReuse> reuseWrapper = Wrappers.lambdaQuery();
        reuseWrapper.eq(RuleRecordReuse::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(RuleRecordReuse::getBuildProjectCode, query.getBuildProjectCode())
                .eq(RuleRecordReuse::getBuildBusinessCode, query.getBuildBusinessCode())
                .eq(RuleRecordReuse::getBuildRuleCode, query.getBuildRuleCode())
                .eq(RuleRecordReuse::getRuleId, query.getRuleId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus, 0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(reuseWrapper);

        // 筛选出此用户下关联的标准规则组id
        List<Integer> standardRuleList = ruleRecordReuseList.stream().map(RuleRecordReuse::getParentCardId).collect(Collectors.toList());

        // 过滤掉此用户已引用的标准规则组
        rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupList.stream().filter(x -> !standardRuleList.contains(x.getId())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(query.getModelAntiStandardGroupDataList())) {
            rdeModelAntiFraudRuleGroupList.addAll(query.getModelAntiStandardGroupDataList());
        }
        if (CollectionUtils.isNotEmpty(query.getModelAntiStandardGroupDataDelList())) {
            rdeModelAntiFraudRuleGroupList.removeAll(query.getModelAntiStandardGroupDataDelList());
        }

        // 搜索
        if (StringUtils.isNotEmpty(query.getName())) {
            List<RdeModelAntiFraudRuleGroupSnapshot> collect = rdeModelAntiFraudRuleGroupList.stream().filter(x -> x.getName().contains(query.getName())).collect(Collectors.toList());
            // 过滤出已开启的
            collect = collect.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
            return collect;
        }
        // 过滤出已开启的
        rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupList.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
        return rdeModelAntiFraudRuleGroupList;
    }

    public List<RdeModelAntiFraudRuleGroupSnapshot> rulePoolList(ModelAntiFraudRuleGroupVO query){
        LambdaQueryWrapper<RdeModelAntiFraudRuleGroupSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleGroupSnapshot::getProjectCode, query.getProjectCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getBusinessCode, query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getRuleCode, query.getRuleCode())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getModelId, query.getModelId())
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudRuleGroupSnapshot::getDataStatus, 0)
                .orderByDesc(RdeModelAntiFraudRuleGroupSnapshot::getUpdateTime);
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupList = snapshotMapper.selectList(wrapper);

        return rdeModelAntiFraudRuleGroupList;
    }

    /**
     * 更新或插入
     * @param record
     * @return
     */
    public int submit(RdeModelAntiFraudRuleGroupVO record, HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (record.getId()==null) {
            record.setCreateUserId(loginUser.getSysUser().getUserId().intValue());
            record.setDeptId(loginUser.getSysUser().getDeptId().intValue());
//            record.setDeptFlag(loginUser.getSysUser().getDeptId().intValue() == Integer.parseInt(adminDeptId)  ? "1" : "2");
            //区分标准策略和自建策略
            record.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? "1" : "2");
            int result = mapper.insert(record);
            return result;
        }else {
            return mapper.updateById(record);
        }
    }


    public RdeModelAntiFraudRuleGroup get(RdeModelAntiFraudRuleGroup query) {
        return mapper.selectById(query.getId());
    }

    public String getRules(Integer id){
        return this.getCodeRules(id);
//        RdeModelAntiFraudRuleRecord ruleRecord = new RdeModelAntiFraudRuleRecord();
//        ruleRecord.setGroupId(id);
//        ruleRecord.setDataStatus((byte) 0);
//        List<RdeModelAntiFraudRuleRecord> ruleRecordList = ruleRecordMapper.select(ruleRecord);
//        if(ruleRecordList==null){
//            return null;
//        }
//        StringBuffer rules = new StringBuffer();
//        StringBuffer packages = new StringBuffer("package com.sample\n");
//        Set<String> packageSet = new HashSet<>();
//
//        List<RdeModelRuleActivityRecord> activityRecordList = null;
//        RdeModelRuleActivityRecord activityRecord = new RdeModelRuleActivityRecord();
//        for (RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord : ruleRecordList) {
//            rules.append(rdeModelAntiFraudRuleRecord.getTermRule()+"\n");
//
//            activityRecord.setRuleId(rdeModelAntiFraudRuleRecord.getId());
//            activityRecord.setDataStatus((byte) 0);
//            activityRecordList = activityRecordMapper.select(activityRecord);
//            if(activityRecordList!=null && !activityRecordList.isEmpty()){
//                for (RdeModelRuleActivityRecord rdeModelRuleActivityRecord : activityRecordList) {
//                    packageSet.add(rdeModelRuleActivityRecord.getPackages());
//                }
//            }
//        }
//        for (String s : packageSet) {
//            packages.append(s+"\n");
//        }
//
//        return packages.toString()+"\n"+rules.toString();
    }
    
    public String getCodeRules(Integer id){

//        RdeModelDecisionRuleRecord ruleRecord = new RdeModelDecisionRuleRecord();
//        ruleRecord.setGroupId(id);
//        ruleRecord.setDataStatus((byte) 0);
        LambdaQueryWrapper<RdeModelDecisionRuleRecord> rdeModelDecisionRuleRecordQueryWrapper = new LambdaQueryWrapper<>();
        rdeModelDecisionRuleRecordQueryWrapper.eq(RdeModelDecisionRuleRecord::getGroupId,id).eq(RdeModelDecisionRuleRecord::getDataStatus,0);
        List<RdeModelDecisionRuleRecord> ruleRecordList = codeRuleMapper.selectList(rdeModelDecisionRuleRecordQueryWrapper);
        if(ruleRecordList==null){
            return null;
        }
        StringBuffer rules = new StringBuffer();
        StringBuffer packages = new StringBuffer("package com.sample\n");
        Set<String> packageSet = new HashSet<>();

        List<RdeModelRuleActivityRecord> activityRecordList = null;
        RdeModelRuleActivityRecord activityRecord = new RdeModelRuleActivityRecord();
        for (RdeModelDecisionRuleRecord record : ruleRecordList) {
            RdeModelDecisionCodeLevel level = codeLevelMapper.selectById(record.getCodeId());
            rules.append(level.getTermRule()+"\n");

//            activityRecord.setCodeId(record.getCodeId());
//            activityRecord.setDataStatus((byte) 0);
            activityRecordList = activityRecordMapper.selectList(new LambdaQueryWrapper<RdeModelRuleActivityRecord>().eq(RdeModelRuleActivityRecord::getCodeId, record.getCodeId()).eq(RdeModelRuleActivityRecord::getDataStatus, (byte) 0));
            if(activityRecordList!=null && !activityRecordList.isEmpty()){
                for (RdeModelRuleActivityRecord rdeModelRuleActivityRecord : activityRecordList) {
                    packageSet.add(rdeModelRuleActivityRecord.getPackages());
                }
            }
        }
        for (String s : packageSet) {
            packages.append(s+"\n");
        }

        return packages.toString()+"\n"+rules.toString();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public RdeModelAntiFraudRuleGroup selectById(Integer id){
//        Example example = new Example(RdeModelAntiFraudRuleGroup.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id",id).andEqualTo("dataStatus", Byte.valueOf("0"));
        List<RdeModelAntiFraudRuleGroup> lists = mapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleGroup>().eq(RdeModelAntiFraudRuleGroup::getId,id).eq(RdeModelAntiFraudRuleGroup::getDataStatus, (byte) 0));
        if(lists!=null && lists.size()>0){
            return lists.get(0);
        }
        return null;
    }

    /**
     * 删除
     * @param record
     * @return
     */
    public int delete(RdeModelAntiFraudRuleGroup record) {
        RdeModelAntiFraudRuleGroup agent = new RdeModelAntiFraudRuleGroup();
        agent.setId(record.getId());
        agent.setDataStatus(Byte.valueOf("1"));
        return mapper.updateById(agent);
    }

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        RdeModelAntiFraudRuleGroup agent = new RdeModelAntiFraudRuleGroup();
        agent.setId(id);
        agent.setDataStatus(Byte.valueOf("1"));
        return mapper.updateById(agent);
    }

}
