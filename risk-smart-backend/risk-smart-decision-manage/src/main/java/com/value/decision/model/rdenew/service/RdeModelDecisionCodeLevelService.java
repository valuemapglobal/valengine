package com.value.decision.model.rdenew.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.constant.HttpStatus;
import com.value.decision.common.utils.security.SecurityUtils;
import com.risksmart.common.core.web.page.TableDataInfo;
import com.value.decision.model.decisionmanage.common.GenerateRuleUtil;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.vo.WarnComparisonVO;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.model.rdenew.domain.*;
import com.value.decision.model.rdenew.mapper.*;
import com.value.decision.model.rdenew.vo.*;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleRecordSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelDecisionCodeLevelSnapshotMapper;
import com.value.decision.common.security.LoginUser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RdeModelDecisionCodeLevelService {
    @Autowired
    private RdeModelDecisionCodeLevelMapper mapper;
    @Autowired
    private RdeModelDecisionCodeLevelSnapshotMapper rdeModelDecisionCodeLevelSnapshotMapper;
    @Autowired
    private RdeRuleActivityRecordMapper activityRecordMapper;
    @Autowired
    private RdeModelRulePropertyMapper propertyMapper;
    @Autowired
    private RdeModelRuleMethodMapper methodMapper;
    @Autowired
    private RdeModelRuleRecordMapper rdeModelRuleRecordMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper rdeModelRuleRecordSnapshotMapper;
    @Autowired
    private RdeRiskVariableThemeMapper rdeRiskVariableThemeMapper;
    @Autowired
    private RdeRiskVariableGroupMapper rdeRiskVariableGroupMapper;
    @Autowired
    private RdeRiskVariableRecordMapper rdeRiskVariableRecordMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper snapshotMapper;
    @Autowired
    private RdeModelDecisionCodeLevelSnapshotMapper snapshotCodeLevelMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordMapper rmapper;
    @Resource
    private RuoYiService ruoYiService;
    @Autowired
    private ProcessNodeMapper processNodeMapper;
    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;


    /**
     * 检查模型是否被部门内其他用户使用
     * @param codeId 模型/规则ID
     * @param deptId 部门ID
     * @return 是否被使用
     */
    public boolean checkIfModelUsedByOthersInDept(Integer codeId, Integer deptId) {
        if (codeId == null || deptId == null) {
            return false;
        }

        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long currentUserId = loginUser.getUserid();

            // 获取当前模型信息
            RdeModelDecisionCodeLevelSnapshot codeLevel = snapshotCodeLevelMapper.selectById(codeId);
            if (codeLevel == null) {
                return false;
            }

            // 查询同部门下是否有其他用户在使用该模型
            LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RdeModelDecisionCodeLevelSnapshot::getDeptId, deptId)
                    .eq(RdeModelDecisionCodeLevelSnapshot::getRuleCode, codeLevel.getRuleCode())
                    .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0)
                    .ne(RdeModelDecisionCodeLevelSnapshot::getCreateBy, currentUserId.toString()); // 排除当前用户

            long count = snapshotCodeLevelMapper.selectCount(queryWrapper);

            // 如果有其他用户在使用，返回true
            return count > 0;
        } catch (Exception e) {
            log.error("检查模型是否被部门内其他用户使用时发生异常", e);
            return false;
        }
    }


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
            RdeModelDecisionCodeLevelSnapshot ruleRecord = snapshotCodeLevelMapper.selectById(recordId);
            if (ruleRecord == null) {
                return false;
            }

            // 2. 通过modelId查询流程节点表是否引用了对应的规则
            LambdaQueryWrapper<ProcessNode> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProcessNode::getRuleCode, ruleRecord.getCode())
                    .eq(ProcessNode::getDataStatus, 0);
            long count = processNodeMapper.selectCount(queryWrapper);

            return count > 0;
        } catch (Exception e) {
            log.error("检查规则明细是否被流程策略引用时发生异常", e);
            return false;
        }
    }

    /**
     * 查询列表
     *
     * @param query
     * @return
     */
    public TableDataInfo list(RdeModelDecisionCodeLevelVO query, HttpServletRequest request) {
        LoginUser loginUser1 = SecurityUtils.getLoginUser();
//        SysUser loginUser = loginUser1.getSysUser();
        int deptId = loginUser1.getSysUser().getDeptId().intValue();
        query.setDeptId(deptId);
        query.setDataStatus(Byte.valueOf("0"));
        /*LoginUser loginUser = com.vm.console.common.utils.securityOne.SecurityUtils.getLoginUser(request);
        if(loginUser.getSysUser().getDeptId()!=null){
            query.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        }*/
        List<RdeModelDecisionCodeLevel> list = mapper.selectListQ(query);
        List<RdeModelDecisionCodeLevelVO> volist = new ArrayList<>();
        list.stream().forEach(x -> {
            RdeModelDecisionCodeLevelVO vo = new RdeModelDecisionCodeLevelVO();
            BeanUtils.copyProperties(x, vo);
            //查询分析对象
            List<RdeRiskVariableTheme> objlist = mapper.selectObjectList(x.getId());
            vo.setObjectArray(objlist);
            volist.add(vo);
        });
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(volist);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    public List<RdeModelDecisionCodeLevel> officialList(RdeModelDecisionCodeLevelVO query) {
        query.setDataStatus(Byte.valueOf("0"));

        query.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId().intValue());
        return mapper.officialList(query);
    }
    //检查code是否存在
    public boolean checkCode(String code) {
        LambdaQueryWrapper<RdeModelDecisionCodeLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RdeModelDecisionCodeLevel::getCode, code).eq(RdeModelDecisionCodeLevel::getDataStatus, 0);
        List<RdeModelDecisionCodeLevel> list = mapper.selectList(queryWrapper);
    	if(list !=null && list.size() > 0) {
    		return true;
    	}else {
    		return false;
    	} 
    }

    //检查code是否存在
    public boolean checkCode(RdeModelDecisionCodeLevelVO code,LoginUser loginUser) {
        int count = mapper.selectList2(code.getId(),code.getProjectCode(),code.getBusinessCode(),code.getRuleCode(),loginUser.getSysUser().getDeptId(),code.getCode(),code.getModuleId());
        if(count > 0) {
            return true;
        }else {
            return false;
        }
    }
    

    /**
     * 更新或插入
     *
     * @param record
     * @return
     */
    @Transactional
    @Deprecated
    public int submit(RdeModelDecisionCodeLevelVO record) {

        if (record.getId() == null) {
            record.setTermRule(this.thePackageRule(record));
            record.setStatus("1");
            int result = mapper.insert(record);

            Set<String> packageSet = new HashSet<>();
            List<RdeModelRuleActivityRecord> termArray = record.getTermArray();
            if (termArray != null) {
                for (RdeModelRuleActivityRecord rdeModelRuleActivityRecord : termArray) {
                    rdeModelRuleActivityRecord.setId(null);
                    rdeModelRuleActivityRecord.setCodeId(record.getId());
                    activityRecordMapper.insert(rdeModelRuleActivityRecord);
                    packageSet.add(rdeModelRuleActivityRecord.getPackages());
                }
            }

            if (!packageSet.isEmpty()) {
                StringBuffer packages = new StringBuffer();
                for (String s : packageSet) {
                    packages.append(s + "\n");
                }
                record.setTermPackage(packages.toString());
                mapper.updateById(record);
            }

            List<RdeModelRuleProperty> propertyArray = record.getPropertyArray();
            if (propertyArray != null) {
                for (RdeModelRuleProperty rdeModelRuleProperty : propertyArray) {
                    rdeModelRuleProperty.setId(null);
                    rdeModelRuleProperty.setCodeId(record.getId());
                    rdeModelRuleProperty.setCreateUserId(SecurityUtils.getLoginUser().getSysUser().getUserId().intValue());
                    propertyMapper.insert(rdeModelRuleProperty);
                }
            }


            List<RdeModelRuleMethod> methodArray = record.getMethodArray();
            if (methodArray != null) {
                for (RdeModelRuleMethod rdeModelRuleMethod : methodArray) {
                    if ((rdeModelRuleMethod.getName() == null || rdeModelRuleMethod.getName().equals(""))
                            && (rdeModelRuleMethod.getObjAlias() == null || rdeModelRuleMethod.getObjAlias().equals(""))
                            && (rdeModelRuleMethod.getKeyCode() == null || rdeModelRuleMethod.getKeyCode().equals(""))
                            && (rdeModelRuleMethod.getKeyValue() == null || rdeModelRuleMethod.getKeyValue().equals(""))) {
                        continue;
                    }
                    rdeModelRuleMethod.setId(null);
                    rdeModelRuleMethod.setCodeId(record.getId());
                    rdeModelRuleMethod.setCreateUserId(SecurityUtils.getLoginUser().getSysUser().getUserId().intValue());
                    methodMapper.insert(rdeModelRuleMethod);

                }
            }

            return result;
        } else {
//            Example example = new Example(RdeModelRuleActivityRecord.class);
//            Example.Criteria criteria = example.createCriteria();
//            criteria.andEqualTo("codeId", record.getId());
//            RdeModelRuleActivityRecord activityRecord = new RdeModelRuleActivityRecord();
//            activityRecord.setDataStatus((byte) 1);
//            activityRecordMapper.updateByExampleSelective(activityRecord, example);

            // 创建 LambdaQueryWrapper 对象
            LambdaQueryWrapper<RdeModelRuleActivityRecord> queryWrapper = new LambdaQueryWrapper<>();

// 设置查询条件
            queryWrapper.eq(RdeModelRuleActivityRecord::getCodeId, record.getId());

// 创建要更新的实体对象
            RdeModelRuleActivityRecord activityRecord = new RdeModelRuleActivityRecord();
            activityRecord.setDataStatus((byte) 1);

// 使用 LambdaQueryWrapper 对象和要更新的实体对象进行更新操作
            activityRecordMapper.update(activityRecord, queryWrapper);

//            Example example2 = new Example(RdeModelRuleProperty.class);
//            Example.Criteria criteria2 = example2.createCriteria();
//            criteria2.andEqualTo("codeId", record.getId());
//            RdeModelRuleProperty property = new RdeModelRuleProperty();
//            property.setDataStatus((byte) 1);
//            propertyMapper.updateByExampleSelective(property, example2);

            // 创建 LambdaQueryWrapper 对象
            LambdaQueryWrapper<RdeModelRuleProperty> queryWrapper2 = new LambdaQueryWrapper<>();

// 设置查询条件
            queryWrapper2.eq(RdeModelRuleProperty::getCodeId, record.getId());

// 创建要更新的实体对象
            RdeModelRuleProperty property = new RdeModelRuleProperty();
            property.setDataStatus((byte) 1);

// 使用 LambdaQueryWrapper 对象和要更新的实体对象进行更新操作
            propertyMapper.update(property, queryWrapper2);

//            Example example3 = new Example(RdeModelRuleProperty.class);
//            Example.Criteria criteria3 = example3.createCriteria();
//            criteria3.andEqualTo("codeId", record.getId());
//            RdeModelRuleMethod modelRuleMethod = new RdeModelRuleMethod();
//            modelRuleMethod.setDataStatus((byte) 1);
//            methodMapper.updateByExampleSelective(modelRuleMethod, example3);

// 创建 LambdaQueryWrapper 对象
            LambdaQueryWrapper<RdeModelRuleMethod> queryWrapper3 = new LambdaQueryWrapper<>();

// 设置查询条件
            queryWrapper3.eq(RdeModelRuleMethod::getCodeId, record.getId());

// 创建要更新的实体对象
            RdeModelRuleMethod modelRuleMethod = new RdeModelRuleMethod();
            modelRuleMethod.setDataStatus((byte) 1);

// 使用 LambdaQueryWrapper 对象和要更新的实体对象进行更新操作
            methodMapper.update(modelRuleMethod, queryWrapper3);

            Set<String> packageSet = new HashSet<>();
            List<RdeModelRuleActivityRecord> termArray = record.getTermArray();
            if (termArray != null) {
                for (RdeModelRuleActivityRecord rdeModelRuleActivityRecord : termArray) {
                    rdeModelRuleActivityRecord.setId(null);
                    rdeModelRuleActivityRecord.setCodeId(record.getId());
                    activityRecordMapper.insert(rdeModelRuleActivityRecord);
                    packageSet.add(rdeModelRuleActivityRecord.getPackages());
                }
            }

            if (!packageSet.isEmpty()) {
                StringBuffer packages = new StringBuffer();
                for (String s : packageSet) {
                    packages.append(s + "\n");
                }
                record.setTermPackage(packages.toString());
            }

            List<RdeModelRuleProperty> propertyArray = record.getPropertyArray();
            if (propertyArray != null) {
                for (RdeModelRuleProperty rdeModelRuleProperty : propertyArray) {
                    rdeModelRuleProperty.setId(null);
                    rdeModelRuleProperty.setCodeId(record.getId());
                    rdeModelRuleProperty.setCreateUserId(SecurityUtils.getLoginUser().getSysUser().getUserId().intValue());
                    propertyMapper.insert(rdeModelRuleProperty);
                }
            }


            List<RdeModelRuleMethod> methodArray = record.getMethodArray();
            if (methodArray != null) {
                for (RdeModelRuleMethod rdeModelRuleMethod : methodArray) {
                    if ((rdeModelRuleMethod.getName() == null || rdeModelRuleMethod.getName().equals(""))
                            && (rdeModelRuleMethod.getObjAlias() == null || rdeModelRuleMethod.getObjAlias().equals(""))
                            && (rdeModelRuleMethod.getKeyCode() == null || rdeModelRuleMethod.getKeyCode().equals(""))
                            && (rdeModelRuleMethod.getKeyValue() == null || rdeModelRuleMethod.getKeyValue().equals(""))) {
                        continue;
                    }
                    rdeModelRuleMethod.setId(null);
                    rdeModelRuleMethod.setCodeId(record.getId());
                    rdeModelRuleMethod.setCreateUserId(SecurityUtils.getLoginUser().getSysUser().getUserId().intValue());
                    methodMapper.insert(rdeModelRuleMethod);
                }
            }

            record.setTermRule(this.thePackageRule(record));

            return mapper.updateById(record);
        }
    }
    @Resource
    private RdeModelDecisionCodeLevelService rdeModelDecisionCodeLevelService;
    @Resource
    private GenerateRuleUtil generateRuleUtil;
    /**
     * 更新或插入
     *
     * @param record
     * @return
     */
    @Transactional
    public int submit(RdeModelDecisionCodeLevelVO2 record,HttpServletRequest request) {

        if (record.getId() == null) {
            //包装规则
            if(record.getGenerateRuleVO() != null ){
                record.getGenerateRuleVO().setCode(record.getCode());
                record.getGenerateRuleVO().setRuleCode(record.getRuleCode());
                record.getGenerateRuleVO().setDataType(record.getDataType());
                record.getGenerateRuleVO().setBusinessCode(record.getBusinessCode());
                record.getGenerateRuleVO().setSalience(record.getSalience());
                record.setConditions(JSON.toJSONString(record.getGenerateRuleVO().getConditionArray()));
                Map<String, Object> stringObjectMap  =  generateRuleUtil.generateRule(record.getGenerateRuleVO());
                if (stringObjectMap!=null) {
                    record.setTermRule(stringObjectMap.get("codeDrl").toString());
                    record.setDataModule(stringObjectMap.get("objList").toString());
                }
            }
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if(loginUser.getSysUser().getDeptId()!=null){
                record.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            }
            record.setCreateBy(loginUser.getSysUser().getUserId().toString());
//            record.setStatus("0");
//            record.setDeptFlag(loginUser.getSysUser().getDeptId().intValue() == 101 ? "1" : "2");
            record.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? "1" : "2");
            record.setTerm(CollectionUtils.isNotEmpty(record.getGenerateRuleVO().getObjResultCompare())? JSON.toJSONString(record.getGenerateRuleVO().getObjResultCompare()):"");
            record.setCategoryType(record.getSalience() != null ? record.getSalience().toString() : "0");
            mapper.insert(record);
            if(StringUtils.isNotBlank(record.getProjectCode())) {
            	//如果不为空时候插入关联表
            	RdeModelAntiFraudRuleRecord rr = new RdeModelAntiFraudRuleRecord();
                rr.setCode(record.getCode());
                rr.setBusinessCode(record.getBusinessCode());
            	rr.setModelId(record.getModelId());
            	rr.setGroupId(record.getGroupId());
            	rr.setCodeId(record.getId());
            	rr.setCode(record.getCode());
            	rr.setName(record.getContent());
            	rr.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            	rr.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? "1" : "2");
            	rr.setProjectCode(record.getProjectCode());
            	rr.setRuleCode(record.getRuleCode());
                rr.setVersionControl(record.getVersionControl());
            	rmapper.insert(rr);

            }
            int result = record.getId();
            return result;
        } else {
            //rde_model_anti_fraud_rule_record更新
            LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RdeModelAntiFraudRuleRecord::getId, record.getRecordId());
            RdeModelAntiFraudRuleRecord ruleRecord = new RdeModelAntiFraudRuleRecord();
            ruleRecord.setCode(record.getCode());
            ruleRecord.setName(record.getContent());
            rmapper.update(ruleRecord, queryWrapper);
            //封装规则
            if(record.getGenerateRuleVO() != null && record.getGenerateRuleVO().getConditionArray()!=null ){
                record.setConditions(JSON.toJSONString(record.getGenerateRuleVO().getConditionArray()));
                record.getGenerateRuleVO().setCode(record.getCode());
                record.getGenerateRuleVO().setRuleCode(record.getRuleCode());
                record.getGenerateRuleVO().setDataType(record.getDataType());
                record.getGenerateRuleVO().setBusinessCode(record.getBusinessCode());
                record.getGenerateRuleVO().setSalience(record.getSalience());
                Map<String, Object> stringObjectMap  =  generateRuleUtil.generateRule(record.getGenerateRuleVO());
                if (stringObjectMap!=null) {
                    record.setTermRule(stringObjectMap.get("codeDrl").toString());
                    record.setDataModule(stringObjectMap.get("objList").toString());
                }
                record.setTerm(CollectionUtils.isNotEmpty(record.getGenerateRuleVO().getObjResultCompare())? JSON.toJSONString(record.getGenerateRuleVO().getObjResultCompare()):"");
            }else{
                record.setTermRule(null);
                record.setDataModule(null);
                record.setConditions(null);
            }
            record.setUpdateTime(null);
            record.setCategoryType(record.getSalience() != null ? record.getSalience().toString() : "0");
            mapper.updateById(record);
            return record.getId();
        }
    }



    public int openUp (RdeModelDecisionCodeLevel codeLevel){
        if (codeLevel.getStatus().equals("0")){
            codeLevel.setStatus("1");
        }else if (codeLevel.getStatus().equals("1")){
            codeLevel.setStatus("0");
        }
        return mapper.updateById(codeLevel);
    }

    public RdeModelDecisionCodeLevelVO2 get(RdeModelDecisionCodeLevel query) {
        RdeModelDecisionCodeLevel level = mapper.selectById(query.getId());
        RdeModelDecisionCodeLevelVO2 vo = new RdeModelDecisionCodeLevelVO2();
        if (level == null) {
            return vo;
        }
        if(level.getConditions()!=null){
            level.setConditionsJSON(JSON.parseArray(level.getConditions()));
        }
        if(level.getTerm()!=null){
            level.setTermWarn(JSON.parseArray(level.getTerm(), WarnComparisonVO.class));
        }
        vo.setSalience(level.getCategoryType() != null ? Integer.parseInt(level.getCategoryType()) : 0);
        BeanUtils.copyProperties(level, vo);
        // 创建 LambdaQueryWrapper 对象
        LambdaQueryWrapper<RdeModelRuleRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件
        queryWrapper.eq(RdeModelRuleRecord::getCodeId, level.getId());
        // 使用 LambdaQueryWrapper 对象进行查询操作
        List<RdeModelRuleRecord> list = rdeModelRuleRecordMapper.selectList(queryWrapper);

        List<RdeRiskVariableConditionVO> cvo = new ArrayList<>();
        List<RdeRiskVariableConditionVO> mvo = new ArrayList<>();

        Map<Integer, List<RdeModelRuleRecord>> data = list.stream().filter(f -> f.getRuleType().intValue() == 1).collect(Collectors.groupingBy(RdeModelRuleRecord::getSorder));
        for (Integer key : data.keySet()) {
            List<RdeModelRuleRecord> newlist = data.get(key);
            List<RdeRiskVariableConditionVO> cvo2 = new ArrayList<>();
            RdeRiskVariableConditionVO v1 = new RdeRiskVariableConditionVO();
            for (int i = 0; i < newlist.size(); i++) {
                RdeModelRuleRecord rr = newlist.get(i);
                RdeRiskVariableConditionVO v2 = new RdeRiskVariableConditionVO();
                v2.setId(rr.getId());
                v2.setOperator(rr.getRuleOperator());
                v2.setResult(rr.getRuleValue());
                v2.setSelectObj(Arrays.asList(rr.getThemeIds().split(",")));
                cvo2.add(v2);
            }
            v1.setId(key);
            v1.setCondition(cvo2);
            cvo.add(v1);
        }
        vo.setConditionArray(cvo);
        //更新
        list.stream().filter(f -> f.getRuleType().intValue() == 3).forEach(x -> {
            RdeRiskVariableConditionVO vom = new RdeRiskVariableConditionVO();
            vom.setId(x.getId());
            vom.setSelectObj(Arrays.asList(x.getThemeIds().split(",")));
            vom.setResult(x.getRuleValue());
            mvo.add(vom);
        });
        vo.setModifyArray(mvo);

        // 创建 LambdaQueryWrapper 对象
        LambdaQueryWrapper<RdeModelRuleProperty> queryWrapper2 = new LambdaQueryWrapper<>();

        // 设置查询条件
        queryWrapper2.eq(RdeModelRuleProperty::getCodeId, level.getId());

        // 使用 LambdaQueryWrapper 对象进行查询操作
        List<RdeModelRuleProperty> propertyArray = propertyMapper.selectList(queryWrapper2);
        vo.setPropertyArray(propertyArray);
        return vo;
    }

    public RdeModelDecisionCodeLevelVO2 getRulePoolDetail(RdeModelDecisionCodeLevel query) {
        RdeModelDecisionCodeLevelSnapshot level = rdeModelDecisionCodeLevelSnapshotMapper.selectById(query.getId());
        RdeModelDecisionCodeLevelVO2 vo = new RdeModelDecisionCodeLevelVO2();
        if (level == null) {
            return vo;
        }
        if(level.getConditions()!=null){
            level.setConditionsJSON(JSON.parseArray(level.getConditions()));
        }
        BeanUtils.copyProperties(level, vo);

        LambdaQueryWrapper<RdeModelRuleRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RdeModelRuleRecord::getCodeId, level.getId());
        List<RdeModelRuleRecord> list = rdeModelRuleRecordMapper.selectList(queryWrapper);
        List<RdeRiskVariableConditionVO> cvo = new ArrayList<>();
        List<RdeRiskVariableConditionVO> mvo = new ArrayList<>();

        Map<Integer, List<RdeModelRuleRecord>> data = list.stream().filter(f -> f.getRuleType().intValue() == 1).collect(Collectors.groupingBy(RdeModelRuleRecord::getSorder));
        for (Integer key : data.keySet()) {
            List<RdeModelRuleRecord> newlist = data.get(key);
            List<RdeRiskVariableConditionVO> cvo2 = new ArrayList<>();
            RdeRiskVariableConditionVO v1 = new RdeRiskVariableConditionVO();
            for (int i = 0; i < newlist.size(); i++) {
                RdeModelRuleRecord rr = newlist.get(i);
                RdeRiskVariableConditionVO v2 = new RdeRiskVariableConditionVO();
                v2.setId(rr.getId());
                v2.setOperator(rr.getRuleOperator());
                v2.setResult(rr.getRuleValue());
                v2.setSelectObj(Arrays.asList(rr.getThemeIds().split(",")));
                cvo2.add(v2);
            }
            v1.setId(key);
            v1.setCondition(cvo2);
            cvo.add(v1);
        }
        vo.setConditionArray(cvo);
        //更新
        list.stream().filter(f -> f.getRuleType().intValue() == 3).forEach(x -> {
            RdeRiskVariableConditionVO vom = new RdeRiskVariableConditionVO();
            vom.setId(x.getId());
            vom.setSelectObj(Arrays.asList(x.getThemeIds().split(",")));
            vom.setResult(x.getRuleValue());
            mvo.add(vom);
        });
        vo.setModifyArray(mvo);
        LambdaQueryWrapper<RdeModelRuleProperty> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(RdeModelRuleProperty::getCodeId, level.getId());
        List<RdeModelRuleProperty> propertyArray = propertyMapper.selectList(queryWrapper2);
        vo.setPropertyArray(propertyArray);
        return vo;
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    public RdeModelDecisionCodeLevelVO selectById(Integer id) {

        // 创建 QueryWrapper 对象
        QueryWrapper<RdeModelDecisionCodeLevel> queryWrapper = new QueryWrapper<>();

        // 设置查询条件
        queryWrapper.eq("id", id).eq("data_status", 0);

        // 使用 QueryWrapper 对象进行查询操作
        List<RdeModelDecisionCodeLevel> lists = mapper.selectList(queryWrapper);
        if (lists != null && lists.size() > 0) {
            RdeModelDecisionCodeLevelVO vo = new RdeModelDecisionCodeLevelVO();
            BeanUtils.copyProperties(lists.get(0), vo);
            // 创建 QueryWrapper 对象
            QueryWrapper<RdeModelRuleActivityRecord> queryWrapper2 = new QueryWrapper<>();

            // 设置查询条件
            queryWrapper2.eq("codeId", id).eq("dataStatus", 0);

            // 设置排序方式
            queryWrapper2.orderByAsc("id");

            // 使用 QueryWrapper 对象进行查询操作
            List<RdeModelRuleActivityRecord> recordList = activityRecordMapper.selectList(queryWrapper2);
            if (recordList != null && !recordList.isEmpty()) {
                vo.setTermArray(recordList);
            }

            // 创建 QueryWrapper 对象
            QueryWrapper<RdeModelRuleProperty> queryWrapper3 = new QueryWrapper<>();

            // 设置查询条件
            queryWrapper3.eq("codeId", id).eq("dataStatus", 0);

            // 设置排序方式
            queryWrapper3.orderByAsc("id");

            // 使用 QueryWrapper 对象进行查询操作
            List<RdeModelRuleProperty> propertyList = propertyMapper.selectList(queryWrapper3);
            if (propertyList != null && !propertyList.isEmpty()) {
                vo.setPropertyArray(propertyList);
            }

            // 创建 QueryWrapper 对象
            QueryWrapper<RdeModelRuleMethod> queryWrapper4 = new QueryWrapper<>();

            // 设置查询条件
            queryWrapper4.eq("codeId", id).eq("dataStatus", 0);

            // 设置排序方式
            queryWrapper4.orderByAsc("id");

            // 使用 QueryWrapper 对象进行查询操作
            List<RdeModelRuleMethod> methodList = methodMapper.selectList(queryWrapper4);
            if (methodList != null && !methodList.isEmpty()) {
                vo.setMethodArray(methodList);
            }

            return vo;
        }
        return null;
    }

    /**
     * 删除
     *
     * @param record
     * @return
     */
    public int delete(RdeModelDecisionCodeLevel record) {
        RdeModelDecisionCodeLevel agent = new RdeModelDecisionCodeLevel();
        agent.setId(record.getId());
        agent.setDataStatus(Byte.valueOf("1"));
        return mapper.updateById(agent);
    }


    /**
     * 逻辑删除
     * @param record
     * @return
     */
    public int logicDelete(RdeModelDecisionCodeLevel record) {

        RdeModelAntiFraudRuleRecord ruleRecord = new RdeModelAntiFraudRuleRecord();
        ruleRecord.setDataStatus(Byte.valueOf("1"));
        LambdaUpdateWrapper<RdeModelAntiFraudRuleRecord> objectLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        objectLambdaUpdateWrapper.eq(RdeModelAntiFraudRuleRecord::getCodeId,record.getId());
        rmapper.update(ruleRecord,objectLambdaUpdateWrapper);

        RdeModelDecisionCodeLevel agent = new RdeModelDecisionCodeLevel();
        agent.setId(record.getId());
        agent.setDataStatus(Byte.valueOf("1"));
        return mapper.updateById(agent);
    }

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        RdeModelDecisionCodeLevel agent = new RdeModelDecisionCodeLevel();
        agent.setId(id);
        agent.setDataStatus(Byte.valueOf("1"));
        // 创建 LambdaQueryWrapper 对象
        LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> queryWrapper = new LambdaQueryWrapper<>();
       // 设置查询条件
        queryWrapper.eq(RdeModelAntiFraudRuleRecord::getCodeId, id);
        // 使用 LambdaQueryWrapper 对象进行删除操作
        rmapper.delete(queryWrapper);
        return mapper.updateById(agent);
    }

    /**
     * 包装规则
     *
     * @param record
     * @return
     */
    @Deprecated
    public String thePackageRule(RdeModelDecisionCodeLevelVO record) {
        List<RdeModelRuleActivityRecord> recordList = record.getTermArray();
        StringBuffer group = new StringBuffer();
        group.append("rule \"RDE-" + record.getCode() + "" + RandomUtil.randomString(4) + "\"\n");

        // 属性
        List<RdeModelRuleProperty> propertyList = record.getPropertyArray();
        for (RdeModelRuleProperty property : propertyList) {
            if (property != null && property.getKeyCode() != null) {
                group.append(property.getKeyCode() + " " + property.getKeyValue() + "\n");
            }
        }

        StringBuffer when = new StringBuffer();
        if (recordList == null || recordList.isEmpty())
            return "";
        // 条件
        Boolean isLx = true;// 是否单独  true单独 false连续
        for (RdeModelRuleActivityRecord rule : recordList) {
            if (StringUtils.isNotBlank(rule.getNextPropertiesOperator())) {
                when.append("    " + rule.getNextPropertiesOperator() + " ");
            }
            if (isLx && StringUtils.isNotBlank(rule.getRuleObjAlias())) {
                when.append("    " + rule.getRuleObjAlias() + " : ");
            } else {
                when.append("    ");
            }
            if (isLx && rule.getRuleObj() != null) {
                when.append(rule.getRuleObj());
            }
            if (isLx && StringUtils.isNotBlank(rule.getLeftBracket())) {
                when.append(rule.getLeftBracket());
            }
            if (rule.getRuleProperties() != null) {
                when.append(rule.getRuleProperties() + " " + rule.getRuleOperator() + " ");
            }
            // 判断值类型
            if (rule.getRuleValue() != null && ("true".equals(rule.getRuleValue().toLowerCase())
                    || "false".equals(rule.getRuleValue().toLowerCase())
                    || StringUtils.isBlank(rule.getRuleValue()))) {
                when.append(rule.getRuleValue());
            } else if (rule.getRuleValue() != null) {
                when.append("'" + rule.getRuleValue() + "'");
            }
            if (StringUtils.isNotBlank(rule.getRightBracket())) {
                when.append(rule.getRightBracket() + "\n");
                isLx = true; // 单独处理
            } else {
                isLx = false;// 连续拼接
            }
        }
        group.append("  when\n");
        group.append(when);
        group.append("  then\n");
        if ((record.getTermValue() != null && !record.getTermValue().equals(""))) {
            group.append("      saveCodeObject(appId,'" + record.getTermValue() + "')\n");
        } else {
            group.append("      saveCodeObject(appId,'" + record.getCode() + "')\n");
        }
        List<RdeModelRuleMethod> methodList = record.getMethodArray();
        if (methodList != null) {
            for (RdeModelRuleMethod rdeModelRuleMethod : methodList) {
                if ((rdeModelRuleMethod.getName() == null || rdeModelRuleMethod.getName().equals(""))
                        && (rdeModelRuleMethod.getObjAlias() == null || rdeModelRuleMethod.getObjAlias().equals(""))
                        && (rdeModelRuleMethod.getKeyCode() == null || rdeModelRuleMethod.getKeyCode().equals(""))
                        && (rdeModelRuleMethod.getKeyValue() == null || rdeModelRuleMethod.getKeyValue().equals(""))) {
                    continue;
                }
                group.append("      " + rdeModelRuleMethod.getName() + "(" + rdeModelRuleMethod.getObjAlias() + ") {"
                        + getUpStr(rdeModelRuleMethod.getKeyCode()) + "('" + rdeModelRuleMethod.getKeyValue() + "');"
                        + "}\n");
            }
        }

        group.append("     System.out.println(\"" + record.getContent() + "\");\n");
        group.append("end");
        group.append("\n");
        group.append("\n");

        return group.toString();
    }


    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return
     */
    private String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    private String getUpStr(String s) {
        if (s != null && s.replaceAll(" ", "").length() > 0) {
            s = "set" + s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
            return s;
        }
        return null;
    }

}
