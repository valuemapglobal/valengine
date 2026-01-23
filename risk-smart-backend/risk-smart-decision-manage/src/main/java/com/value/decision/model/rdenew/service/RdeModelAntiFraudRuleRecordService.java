package com.value.decision.model.rdenew.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.model.rdenew.domain.*;
import com.value.decision.model.rdenew.mapper.*;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordListVO;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordVO;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleRecordSnapshotMapper;
import com.value.decision.version.domain.RdeModelAntiFraudRuleRecordVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudRuleRecordVersionMapper;
import com.value.decision.common.security.LoginUser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
public class RdeModelAntiFraudRuleRecordService {
    @Autowired
    private RdeModelAntiFraudRuleRecordMapper mapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper snapshotMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordVersionMapper versionMapper;
    @Autowired
    private RdeRuleActivityRecordMapper activityRecordMapper;
    @Autowired
    private RdeModelRulePropertyMapper propertyMapper;
    @Autowired
    private RdeModelRuleMethodMapper methodMapper;
    @Autowired
    private RdeModelAntiFraudService antiFraudService;
    @Autowired
    private RdeModelDecisionCodeLevelMapper codeLevelMapper;

    @Autowired
    RdeModelAntiFraudRuleRecordMapper rdeModelAntiFraudRuleRecordMapper;

    @Autowired
    private RdeModelAntiProjectFraudMapper rdeModelAntiProjectFraudMapper;
    @Autowired
    private RdeModelAntiProjectFraudMapper pfmapper;

    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;

    @Resource
    private RuoYiService ruoYiService;

    @Autowired
    private ProcessPolicyReferenceService processPolicyReferenceService;

    /**
     * 检查规则明细是否被流程策略引用
     * @param recordId 规则明细ID
     * @return 是否被引用
     */
    public boolean checkIfReferencedByProcessPolicy(Integer recordId) {
        if (recordId == null) {
            return false;
        }

        try {
            // 1. 通过规则明细ID查找对应的modelId
            RdeModelAntiFraudRuleRecordSnapshot ruleRecord = snapshotMapper.selectById(recordId);
            if (ruleRecord == null) {
                return false;
            }

            // 2. 通过modelId查询流程节点表是否引用了对应的规则
            return processPolicyReferenceService.checkIfReferencedByProcessPolicy(ruleRecord.getModelId());
        } catch (Exception e) {
            log.error("检查规则明细是否被流程策略引用时发生异常", e);
            return false;
        }
    }


    /**
     * 查询列表
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleRecord> newList(RdeModelAntiFraudRuleRecordVO query){
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleRecord::getModelId,query.getModelId())
                .eq(RdeModelAntiFraudRuleRecord::getGroupId,query.getGroupId())
                .eq(RdeModelAntiFraudRuleRecord::getProjectCode,query.getProjectCode())
                .eq(RdeModelAntiFraudRuleRecord::getBusinessCode,query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleRecord::getRuleCode,query.getRuleCode())
//                .eq(RdeModelAntiFraudRuleRecord::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(RdeModelAntiFraudRuleRecord::getDataStatus,0);
        List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = mapper.selectList(wrapper);

        //关联表查询是否有引用标准规则
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,query.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,query.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,query.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getRuleId,query.getModelId())
                .eq(RuleRecordReuse::getGroupId,query.getGroupId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (CollectionUtils.isNotEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudRuleRecordVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getStatus,1);
                RdeModelAntiFraudRuleRecordVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraudRuleRecord scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraudRuleRecord.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudRuleRecordList.add(scoreCardRecordReuse);
            });
        }

        //搜索
        if (StringUtils.isNotEmpty(query.getCode())){
            List<RdeModelAntiFraudRuleRecord> collect = rdeModelAntiFraudRuleRecordList.stream().filter(x -> x.getCode().contains(query.getCode())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudRuleRecordList;
    }

    public List<RdeModelAntiFraudRuleRecordSnapshot> rulePoolList(RdeModelAntiFraudRuleRecordVO query){
        LambdaQueryWrapper<RdeModelAntiFraudRuleRecordSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleRecordSnapshot::getProjectCode, query.getProjectCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getBusinessCode, query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getRuleCode, query.getRuleCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getModelId, query.getModelId())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getGroupId, query.getGroupId())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getDataStatus, 0)
                .orderByDesc(RdeModelAntiFraudRuleRecordSnapshot::getUpdateTime);
        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudList = snapshotMapper.selectList(wrapper);

        return rdeModelAntiFraudList;
    }

    /**
     * 查询自建列表
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleRecord> newBuildList(RdeModelAntiFraudRuleRecordVO query){
        LoginUser loginUser = SecurityUtils.getLoginUser();

        LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleRecord::getModelId,query.getModelId())
                .eq(RdeModelAntiFraudRuleRecord::getGroupId,query.getGroupId())
                .eq(RdeModelAntiFraudRuleRecord::getProjectCode,query.getProjectCode())
                .eq(RdeModelAntiFraudRuleRecord::getBusinessCode,query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleRecord::getRuleCode,query.getRuleCode())
                .eq(RdeModelAntiFraudRuleRecord::getDataStatus,0);
        List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = mapper.selectList(wrapper);

        //关联表查询是否有引用标准规则
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,query.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,query.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,query.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getRuleId,query.getModelId())
                .eq(RuleRecordReuse::getGroupId,query.getGroupId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (CollectionUtils.isNotEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudRuleRecordVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudRuleRecordVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudRuleRecordVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudRuleRecordVersion::getStatus,1);
                RdeModelAntiFraudRuleRecordVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraudRuleRecord scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraudRuleRecord.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudRuleRecordList.add(scoreCardRecordReuse);
            });
        }

        if (CollectionUtils.isNotEmpty(query.getModelAntiRecordDataList())){
            rdeModelAntiFraudRuleRecordList.addAll(query.getModelAntiRecordDataList());
        }
        if (CollectionUtils.isNotEmpty(query.getModelAntiRecordDataDelList())){
            rdeModelAntiFraudRuleRecordList.removeAll(query.getModelAntiRecordDataDelList());
        }

        //搜索
        if (StringUtils.isNotEmpty(query.getCode())){
            List<RdeModelAntiFraudRuleRecord> collect = rdeModelAntiFraudRuleRecordList.stream().filter(x -> x.getCode().contains(query.getCode())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudRuleRecordList;
    }

    /**
     * 规则列表查询
     * @param query
     * @return
     */
    public List<RdeModelAntiFraudRuleRecordSnapshot> standardList(RdeModelAntiFraudRuleRecordVO query) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LambdaQueryWrapper<RdeModelAntiFraudRuleRecordSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudRuleRecordSnapshot::getProjectCode, query.getProjectCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getBusinessCode, query.getBusinessCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getRuleCode, query.getRuleCode())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getModelId, query.getModelId())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getGroupId, query.getGroupId())
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getDeptFlag, RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudRuleRecordSnapshot::getDataStatus, 0)
                .orderByDesc(RdeModelAntiFraudRuleRecordSnapshot::getUpdateTime);
        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudList = snapshotMapper.selectList(wrapper);

        // 查询当前用户的复用关联表数据
        LambdaQueryWrapper<RuleRecordReuse> reuseWrapper = Wrappers.lambdaQuery();
        reuseWrapper.eq(RuleRecordReuse::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(RuleRecordReuse::getBuildProjectCode, query.getBuildProjectCode())
                .eq(RuleRecordReuse::getBuildBusinessCode, query.getBuildBusinessCode())
                .eq(RuleRecordReuse::getBuildRuleCode, query.getBuildRuleCode())
                .eq(RuleRecordReuse::getRuleId, query.getBuildRuleId())
                .eq(RuleRecordReuse::getGroupId, query.getBuildGroupId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.RULE_LIST)
                .eq(RuleRecordReuse::getDataStatus, 0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(reuseWrapper);

        // 筛选出此用户下关联的标准规则id
        List<Integer> standardRuleList = ruleRecordReuseList.stream().map(RuleRecordReuse::getParentCardId).collect(Collectors.toList());

        // 过滤掉此用户已引用的标准规则
        rdeModelAntiFraudList = rdeModelAntiFraudList.stream().filter(x -> !standardRuleList.contains(x.getId())).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(query.getModelAntiStandardRecordDataList())) {
            rdeModelAntiFraudList.addAll(query.getModelAntiStandardRecordDataList());
        }
        if (CollectionUtils.isNotEmpty(query.getModelAntiStandardRecordDataDelList())) {
            rdeModelAntiFraudList.removeAll(query.getModelAntiStandardRecordDataDelList());
        }

        // 搜索
        if (StringUtils.isNotEmpty(query.getCode())) {
            List<RdeModelAntiFraudRuleRecordSnapshot> collect = rdeModelAntiFraudList.stream().filter(x -> x.getCode().contains(query.getCode())).collect(Collectors.toList());
            // 过滤出已开启的
            collect = collect.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
            return collect;
        }
        // 过滤出已开启的
        rdeModelAntiFraudList = rdeModelAntiFraudList.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
        return rdeModelAntiFraudList;
    }



    public RdeModelAntiFraudRuleRecord get(RdeModelAntiFraudRuleRecord query) {
        return mapper.selectById(query.getId());
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public RdeModelAntiFraudRuleRecordVO selectById(Integer id){
//        Example example = new Example(RdeModelAntiFraudRuleRecord.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id",id).andEqualTo("dataStatus", Byte.valueOf("0"));
        List<RdeModelAntiFraudRuleRecord> lists = mapper.selectList(new LambdaQueryWrapper<RdeModelAntiFraudRuleRecord>()
                .eq(RdeModelAntiFraudRuleRecord::getId,id).eq(RdeModelAntiFraudRuleRecord::getDataStatus, 0));
        if(lists!=null && lists.size()>0){
            RdeModelAntiFraudRuleRecordVO vo = new RdeModelAntiFraudRuleRecordVO();
            BeanUtils.copyProperties(lists.get(0), vo);
           /* RdeRuleActivityRecord record = new RdeRuleActivityRecord();
            record.setGroupId(id);
            record.setDataStatus((byte) 0);*/
//            Example example2 = new Example(RdeModelRuleActivityRecord.class);
//            Example.Criteria criteria2 = example2.createCriteria();
//            criteria2.andEqualTo("ruleId", id).andEqualTo("dataStatus", 0);
//            example2.setOrderByClause(" id");
            QueryWrapper<RdeModelRuleActivityRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ruleId", id).eq("dataStatus", 0).orderByAsc("id");
            List<RdeModelRuleActivityRecord> recordList = activityRecordMapper.selectList(queryWrapper);
            if(recordList!=null && !recordList.isEmpty()){
                vo.setTermArray(recordList);
            }

//            Example example3 = new Example(RdeModelRuleProperty.class);
//            Example.Criteria criteria3 = example3.createCriteria();
//            criteria3.andEqualTo("ruleId", id).andEqualTo("dataStatus", 0);
//            example3.setOrderByClause(" id");
//           ;
            List<RdeModelRuleProperty> propertyList = propertyMapper.selectList( new QueryWrapper<RdeModelRuleProperty>().eq("ruleId", id).eq("dataStatus", 0).orderByAsc("id"));
            if(propertyList!=null && !propertyList.isEmpty()){
                vo.setPropertyArray(propertyList);
            }

//            Example example4 = new Example(RdeModelRuleMethod.class);
//            Example.Criteria criteria4 = example4.createCriteria();
//            criteria4.andEqualTo("ruleId", id).andEqualTo("dataStatus", 0);
//            example4.setOrderByClause(" id");
            List<RdeModelRuleMethod> methodList = methodMapper.selectList(new QueryWrapper<RdeModelRuleMethod>().eq("ruleId", id).eq("dataStatus", 0).orderByAsc("id"));
            if(methodList!=null && !methodList.isEmpty()){
                vo.setMethodArray(methodList);
            }


            return vo;
        }
        return null;
    }

    /**
     * 删除
     * @param record
     * @return
     */
    public int delete(RdeModelAntiFraudRuleRecord record) {
        RdeModelAntiFraudRuleRecord agent = new RdeModelAntiFraudRuleRecord();
//        Example example = new Example(RdeModelAntiFraudRuleRecord.class);
//        Example.Criteria criteria = example.createCriteria();
        QueryWrapper<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordQueryWrapper = new QueryWrapper<>();
        if(record.getId() !=null) {
//            criteria.andEqualTo("id",record.getId());
            rdeModelAntiFraudRuleRecordQueryWrapper.eq("id",record.getId());
        }
        if(record.getModelId() !=null) {
//            criteria.andEqualTo("modelId",record.getModelId());
            rdeModelAntiFraudRuleRecordQueryWrapper.eq("modelId",record.getModelId());
        }
        agent.setDataStatus(Byte.valueOf("1"));
        mapper.update(agent, rdeModelAntiFraudRuleRecordQueryWrapper);
        return 1;
    }

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        RdeModelAntiFraudRuleRecord agent = new RdeModelAntiFraudRuleRecord();
        agent.setId(id);
        agent.setDataStatus(Byte.valueOf("1"));
//        Example example = new Example(RdeModelAntiFraudRuleRecord.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("id", id);
        RdeModelAntiFraudRuleRecord record = mapper.selectById(id);
        RdeModelDecisionCodeLevel level = new RdeModelDecisionCodeLevel();
        level.setId(record.getCodeId());
        level.setDataStatus(Byte.valueOf("1"));
        mapper.updateById(agent);
        return codeLevelMapper.updateById(level);
    }

    /**
     * 包装规则
     * @param record
     * @return
     */
    public String thePackageRule(RdeModelAntiFraudRuleRecordVO record) {
        List<RdeModelRuleActivityRecord> recordList = record.getTermArray();
        StringBuffer group = new StringBuffer();
        group.append("rule \""+record.getName()+"\"\n");

        // 属性
        List<RdeModelRuleProperty> propertyList = record.getPropertyArray();
        if(propertyList  !=null) {
            for (RdeModelRuleProperty property : propertyList) {
                if(property !=null && property.getKeyCode() !=null) {
                    group.append(property.getKeyCode()+" "+property.getKeyValue()+"\n");
                }
            }
        }

        StringBuffer when = new StringBuffer();
        if(recordList==null || recordList.isEmpty()) {
            return "";
        }
        // 条件
        Boolean isLx =true;// 是否单独  true单独 false连续
        for (RdeModelRuleActivityRecord rule : recordList) {
            if(StringUtils.isNotBlank(rule.getNextPropertiesOperator())) {
                when.append("    "+rule.getNextPropertiesOperator()+" ");
            }
            if(isLx && StringUtils.isNotBlank(rule.getRuleObjAlias())) {
                when.append("    "+rule.getRuleObjAlias()+" : ");
            }else {
                when.append("    ");
            }
            if(isLx && rule.getRuleObj()!=null) {
                when.append(rule.getRuleObj());
            }
            if(isLx  && StringUtils.isNotBlank(rule.getLeftBracket())) {
                when.append(rule.getLeftBracket());
            }
            if(rule.getRuleProperties()!=null){
                when.append(rule.getRuleProperties()+" "+rule.getRuleOperator()+" ");
            }
            // 判断值类型
            if(rule.getRuleValue()!=null && ("true".equals(rule.getRuleValue().toLowerCase())
                    || "false".equals(rule.getRuleValue().toLowerCase())
                    || StringUtils.isBlank(rule.getRuleValue()))) {
                when.append(rule.getRuleValue());
            }else if(rule.getRuleValue()!=null){
                when.append("'"+rule.getRuleValue()+"'");
            }
            if(StringUtils.isNotBlank(rule.getRightBracket())) {
                when.append(rule.getRightBracket()+"\n");
                isLx = true; // 单独处理
            }else {
                isLx = false;// 连续拼接
            }
        }
        group.append("  when\n");
        group.append(when);
        group.append("  then\n");
        if((record.getTermValue()!=null && !record.getTermValue().equals(""))){
            group.append("      saveCodeObject(appId,'"+record.getTermValue()+"')\n");
        }
        List<RdeModelRuleMethod> methodList = record.getMethodArray();
        if(methodList!=null){
            for (RdeModelRuleMethod rdeModelRuleMethod : methodList) {
                if((rdeModelRuleMethod.getName()==null || rdeModelRuleMethod.getName().equals(""))
                        && (rdeModelRuleMethod.getObjAlias()==null || rdeModelRuleMethod.getObjAlias().equals(""))
                        && (rdeModelRuleMethod.getKeyCode()==null || rdeModelRuleMethod.getKeyCode().equals(""))
                        && (rdeModelRuleMethod.getKeyValue()==null || rdeModelRuleMethod.getKeyValue().equals(""))) {
                    continue;
                }
                group.append("      " + rdeModelRuleMethod.getName() + "(" + rdeModelRuleMethod.getObjAlias() + ") {"
                        + getUpStr(rdeModelRuleMethod.getKeyCode()) + "('" + rdeModelRuleMethod.getKeyValue() + "');"
                        + "}\n");
            }
        }

        group.append("     System.out.println(\""+record.getDescr()+"\");\n");
        group.append("end");
        group.append("\n");
        group.append("\n");

        return group.toString();
    }

    private String getUpStr(String s){
        if(s!=null && s.replaceAll(" ", "").length()>0){
            s = "set"+s.substring(0, 1).toUpperCase()+s.substring(1, s.length());
            return s;
        }
        return null;
    }

    public int submitInsert(RdeModelAntiFraudRuleRecordListVO rdeModelAntiFraudRuleRecords,HttpServletRequest request) {

        List<RdeModelAntiFraudRuleRecordVO> rdeModelAntiFraudRuleRecordsCon = rdeModelAntiFraudRuleRecords.getRdeModelAntiFraudRuleRecords();

        rdeModelAntiFraudRuleRecordMapper.deleteByGroupAndModule(rdeModelAntiFraudRuleRecordsCon.get(0).getModelId(),rdeModelAntiFraudRuleRecordsCon.get(0).getGroupId());

        LoginUser loginUser = SecurityUtils.getLoginUser();
        for(int i=0;i<rdeModelAntiFraudRuleRecordsCon.size();i++){
            RdeModelAntiFraudRuleRecordVO record = rdeModelAntiFraudRuleRecordsCon.get(i);
            record.setCreateUserId(loginUser.getSysUser().getUserId().intValue());
            record.setTermRule(this.thePackageRule(record));
            record.setDeptFlag(loginUser.getSysUser().getDeptId() == 101 ? "1" : "2");
            mapper.insert(record);
            /*if(StringUtils.isNotBlank(record.getProjectCode())) {

            	//如果不为空时候插入关联表
            	RdeModelAntiProjectFraud pf = new RdeModelAntiProjectFraud();
            	pf.setProjectCode(record.getProjectCode());
            	pf.setModelId(record.getModelId());
            	pf.setGroupId(record.getGroupId());
            	pf.setRuleId(record.getId());
            	pf.setUserId(loginUser.getUserId().intValue());
            	pf.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                pfmapper.insertSelective(pf);
            }*/
            Set<String> packageSet = new HashSet<>();
            List<RdeModelRuleActivityRecord> termArray = record.getTermArray();
            if(termArray !=null) {
                for (RdeModelRuleActivityRecord rdeModelRuleActivityRecord : termArray) {
                    rdeModelRuleActivityRecord.setId(null);
                    rdeModelRuleActivityRecord.setRuleId(record.getId());
                    activityRecordMapper.insert(rdeModelRuleActivityRecord);
                    packageSet.add(rdeModelRuleActivityRecord.getPackages());
                }
            }
            if(!packageSet.isEmpty()){
                StringBuffer packages = new StringBuffer();
                for (String s : packageSet) {
                    packages.append(s+"\n");
                }
                record.setTermPackage(packages.toString());
                mapper.updateById(record);
            }

            List<RdeModelRuleProperty> propertyArray = record.getPropertyArray();
            if(propertyArray !=null) {
                for (RdeModelRuleProperty rdeModelRuleProperty : propertyArray) {
                    rdeModelRuleProperty.setId(null);
                    rdeModelRuleProperty.setRuleId(record.getId());
                    rdeModelRuleProperty.setCreateUserId(loginUser.getSysUser().getUserId().intValue());
                    propertyMapper.insert(rdeModelRuleProperty);
                }
            }

            List<RdeModelRuleMethod> methodArray = record.getMethodArray();
            if(methodArray!=null) {
                for (RdeModelRuleMethod rdeModelRuleMethod : methodArray) {
                    if((rdeModelRuleMethod.getName()==null || rdeModelRuleMethod.getName().equals(""))
                            && (rdeModelRuleMethod.getObjAlias()==null || rdeModelRuleMethod.getObjAlias().equals(""))
                            && (rdeModelRuleMethod.getKeyCode()==null || rdeModelRuleMethod.getKeyCode().equals(""))
                            && (rdeModelRuleMethod.getKeyValue()==null || rdeModelRuleMethod.getKeyValue().equals(""))) {
                        continue;
                    }
                    rdeModelRuleMethod.setId(null);
                    rdeModelRuleMethod.setRuleId(record.getId());
                    rdeModelRuleMethod.setCreateUserId(loginUser.getSysUser().getUserId().intValue());
                    methodMapper.insert(rdeModelRuleMethod);

                }
            }
            if(record.getModelId()!=null){
            	RdeModelAntiFraudVO antiFraud = new RdeModelAntiFraudVO();
                antiFraud.setId(record.getModelId());
                antiFraud.setCheckStatus((byte) 0);
                antiFraudService.submit(antiFraud,request);
            }


        }

        return 1;
    }
}
