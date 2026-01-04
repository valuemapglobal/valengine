package com.value.decision.model.rdenew.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.mapper.RuleRecordReuseMapper;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudVO;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudVO;
import com.value.decision.model.rdenew.vo.RdeUpdateStateEntryVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.value.decision.version.common.util.StringUtil;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudVersionMapper;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RdeModelAntiFraudService {

    private Logger logger = LoggerFactory.getLogger(RdeModelAntiFraudService.class);

    @Autowired
    private RdeModelAntiFraudMapper mapper;
    @Autowired
    private RdeModelAntiFraudSnapshotMapper snapshotMapper;
    @Autowired
    private RdeModelAntiFraudVersionMapper versionMapper;
    @Autowired
    private ProcessPolicyReferenceService processPolicyReferenceService;
    @Resource
    private RuoYiService ruoYiService;
    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupMapper ruleGroupMapper;
    @Autowired
    private RdeModelAntiFraudRuleRecordMapper ruleRecordMapper;


    /**
     * 查询列表
     *
     * @param
     * @return
     */
    public List<RdeModelAntiFraud> newlist(ModelAntiFraudVO rdeModelAntiFraud) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
//        List<RdeModelAntiFraud> rdeModelAntiFrauds = mapper.selectByprojectCodeAndByModelType2(modelType, loginUser.getSysUser().getDeptId().intValue(), projectCode, ruleCode,businessCode);
        LambdaQueryWrapper<RdeModelAntiFraud> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraud::getProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RdeModelAntiFraud::getBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RdeModelAntiFraud::getRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RdeModelAntiFraud::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(RdeModelAntiFraud::getDataStatus,0)
                .orderByDesc(RdeModelAntiFraud::getUpdateTime);
        List<RdeModelAntiFraud> rdeModelAntiFraudList = mapper.selectList(wrapper);
        //关联表查询是否有引用标准策略
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.POLICY_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudVersion::getStatus,1);
                RdeModelAntiFraudVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraud scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraud.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudList.add(scoreCardRecordReuse);
            });
        }

        //搜索
        if (com.value.decision.common.utils.StringUtils.isNotEmpty(rdeModelAntiFraud.getName())){
            List<RdeModelAntiFraud> collect = rdeModelAntiFraudList.stream().filter(x -> x.getName().contains(rdeModelAntiFraud.getName())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudList;
    }

    /**
     * 规则复用-自建列表
     *
     * @param
     * @return
     */
    @Transactional
    public List<RdeModelAntiFraud> newBuildList(ModelAntiFraudVO rdeModelAntiFraud) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LambdaQueryWrapper<RdeModelAntiFraud> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraud::getProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RdeModelAntiFraud::getBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RdeModelAntiFraud::getRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RdeModelAntiFraud::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(RdeModelAntiFraud::getDataStatus,0)
                .orderByDesc(RdeModelAntiFraud::getUpdateTime);
        List<RdeModelAntiFraud> rdeModelAntiFraudList = mapper.selectList(wrapper);
        //关联表查询是否有引用标准策略
        LambdaQueryWrapper<RuleRecordReuse> wrapperReuse = Wrappers.lambdaQuery();
        wrapperReuse.eq(RuleRecordReuse::getBuildBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RuleRecordReuse::getBuildProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RuleRecordReuse::getBuildRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId())
                .eq(RuleRecordReuse::getMoudleId, RuleConstants.POLICY_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(wrapperReuse);
        if (!CollectionUtils.isEmpty(ruleRecordReuseList)){
            ruleRecordReuseList.stream().forEach(x ->{
                LambdaQueryWrapper<RdeModelAntiFraudVersion> wrapperVersion = Wrappers.lambdaQuery();
                wrapperVersion.eq(RdeModelAntiFraudVersion::getId,x.getParentCardId())
                        .eq(RdeModelAntiFraudVersion::getDeptFlag,1)
                        .eq(RdeModelAntiFraudVersion::getVersionControl,x.getVersionControl())
                        .eq(RdeModelAntiFraudVersion::getStatus,1);
                RdeModelAntiFraudVersion ruleRecordReuseVersion = versionMapper.selectOne(wrapperVersion);
                RdeModelAntiFraud scoreCardRecordReuse = JSONObject.parseObject(JSON.toJSONString(ruleRecordReuseVersion), RdeModelAntiFraud.class);
                scoreCardRecordReuse.setStatus(x.getButtonState() == 0?"0":"1");
                rdeModelAntiFraudList.add(scoreCardRecordReuse);
            });
        }

        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(rdeModelAntiFraud.getModelAntiDataList())){
            rdeModelAntiFraudList.addAll(rdeModelAntiFraud.getModelAntiDataList());
        }
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(rdeModelAntiFraud.getModelAntiDataDelList())){
            rdeModelAntiFraudList.removeAll(rdeModelAntiFraud.getModelAntiDataDelList());
        }

        //搜索
        if (StringUtils.isNotEmpty(rdeModelAntiFraud.getName())){
            List<RdeModelAntiFraud> collect = rdeModelAntiFraudList.stream().filter(x -> x.getName().contains(rdeModelAntiFraud.getName())).collect(Collectors.toList());
            return collect;
        }

        return rdeModelAntiFraudList;
    }


    /**
     * 规则复用-标准列表
     *
     * @param
     * @return
     */
    @Transactional
    public List<RdeModelAntiFraudSnapshot> standardList(ModelAntiFraudVO rdeModelAntiFraud) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        LambdaQueryWrapper<RdeModelAntiFraudSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudSnapshot::getProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RdeModelAntiFraudSnapshot::getBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RdeModelAntiFraudSnapshot::getRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RdeModelAntiFraudSnapshot::getDeptFlag,RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudSnapshot::getDataStatus,0)
                .orderByDesc(RdeModelAntiFraudSnapshot::getUpdateTime);
        List<RdeModelAntiFraudSnapshot> rdeModelAntiFraudList = snapshotMapper.selectList(wrapper);
        //查询当前用户的复用关联表数据
        LambdaQueryWrapper<RuleRecordReuse> reuseWrapper = Wrappers.lambdaQuery();
        reuseWrapper.eq(RuleRecordReuse::getDeptId,loginUser.getSysUser().getDeptId().intValue())
                .eq(RuleRecordReuse::getBuildProjectCode,rdeModelAntiFraud.getBuildProjectCode())
                .eq(RuleRecordReuse::getBuildBusinessCode,rdeModelAntiFraud.getBuildBusinessCode())
                .eq(RuleRecordReuse::getBuildRuleCode,rdeModelAntiFraud.getBuildRuleCode())
                .eq(RuleRecordReuse::getMoudleId,RuleConstants.POLICY_GROUP_LIST)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordReuseList = ruleRecordReuseMapper.selectList(reuseWrapper);

        //筛选出此用户下关联的标准评分卡id
        List<Integer> standardRuleList = ruleRecordReuseList.stream().map(RuleRecordReuse::getParentCardId).collect(Collectors.toList());

        //过滤掉此用户已引用的标准评分卡
        rdeModelAntiFraudList = rdeModelAntiFraudList.stream().filter(x -> !standardRuleList.contains(x.getId())).collect(Collectors.toList());

        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(rdeModelAntiFraud.getModelAntiStandardDataList())){
            rdeModelAntiFraudList.addAll(rdeModelAntiFraud.getModelAntiStandardDataList());
        }
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(rdeModelAntiFraud.getModelAntiStandardDataDelList())){
            rdeModelAntiFraudList.removeAll(rdeModelAntiFraud.getModelAntiStandardDataDelList());
        }

        //搜索
        if (StringUtils.isNotEmpty(rdeModelAntiFraud.getName())){
            List<RdeModelAntiFraudSnapshot> collect = rdeModelAntiFraudList.stream().filter(x -> x.getName().contains(rdeModelAntiFraud.getName())).collect(Collectors.toList());
            //过滤出已开启的
            collect = collect.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
            return collect;
        }
        //过滤出已开启的
        rdeModelAntiFraudList = rdeModelAntiFraudList.stream().filter(x -> "1".equals(x.getStatus())).collect(Collectors.toList());
        return rdeModelAntiFraudList;
    }

    public List<RdeModelAntiFraudSnapshot> rulePoolList(ModelAntiFraudVO rdeModelAntiFraud){
        LambdaQueryWrapper<RdeModelAntiFraudSnapshot> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RdeModelAntiFraudSnapshot::getProjectCode,rdeModelAntiFraud.getProjectCode())
                .eq(RdeModelAntiFraudSnapshot::getBusinessCode,rdeModelAntiFraud.getBusinessCode())
                .eq(RdeModelAntiFraudSnapshot::getRuleCode,rdeModelAntiFraud.getRuleCode())
                .eq(RdeModelAntiFraudSnapshot::getDeptFlag,RuleConstants.RULE_STANDARD)
                .eq(RdeModelAntiFraudSnapshot::getDataStatus,0)
                .orderByDesc(RdeModelAntiFraudSnapshot::getUpdateTime);
        List<RdeModelAntiFraudSnapshot> rdeModelAntiFraudList = snapshotMapper.selectList(wrapper);

        return rdeModelAntiFraudList;
    }

    public AjaxResult checkName(RdeModelAntiFraud record, LoginUser loginUser) {
        if (record.getBusinessCode() == null || record.getName() == null
                || record.getRuleCode() == null || record.getProjectCode() == null) {
            return AjaxResult.error("参数缺失");
        }
        //同部门同业务场景同名验证
        LambdaQueryWrapper<RdeModelAntiFraud> eq = new LambdaQueryWrapper<RdeModelAntiFraud>()
                .eq(RdeModelAntiFraud::getDeptId, loginUser.getSysUser().getDeptId().intValue())
                .eq(RdeModelAntiFraud::getBusinessCode, record.getBusinessCode())
                .eq(RdeModelAntiFraud::getName, record.getName())
                .eq(RdeModelAntiFraud::getRuleCode, record.getRuleCode())
                .eq(RdeModelAntiFraud::getProjectCode, record.getProjectCode())
                .eq(RdeModelAntiFraud::getDataStatus,0);
        if (record.getId() != null) {
            eq.ne(RdeModelAntiFraud::getId, record.getId());
        } else {
            eq.isNotNull(RdeModelAntiFraud::getId);
        }
        if (mapper.selectList(eq).size() != 0) {
            return AjaxResult.success(true);
        }
        return AjaxResult.success(false);
    }

    /**
     * 更新或插入
     *
     * @param record
     * @return
     */
    public int submit(RdeModelAntiFraudVO record, HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (record.getId() == null) {
            if (StringUtils.isNotBlank(record.getProjectCode())) {
                record.setStatus("1");
            } else {
                record.setStatus("0");
            }
            record.setCreateUserId(loginUser.getSysUser().getUserId().intValue());
            record.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            //区分标准策略和自建策略
            record.setDeptFlag(ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId()) ? "1" : "2");
            //最新版本号去通过版本生成方法生成修改后的版本号
            String versionNum = StringUtil.getVersionNum(null,record.getProjectName(),record.getBusinessName(),record.getPersonOrCompany());
            record.setVersionControl(versionNum);
            return mapper.insert(record);
        } else {
            return mapper.updateById(record);
        }
    }



    public RdeModelAntiFraud get(RdeModelAntiFraud query) {
        return mapper.selectById(query.getId());
    }


    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    public RdeModelAntiFraud selectById(Integer id) {
        QueryWrapper<RdeModelAntiFraud> queryWrapper = new QueryWrapper<>();
        // 设置查询条件
        //queryWrapper.eq("id", id).eq("dataStatus", 0);
        queryWrapper.eq("id", id).eq("data_status", 0);
        // 使用 QueryWrapper 对象进行查询
        List<RdeModelAntiFraud> lists = mapper.selectList(queryWrapper);
        if (lists != null && lists.size() > 0) {
            return lists.get(0);
        }
        return null;
    }

    /**
     * 删除
     *
     * @param record
     * @return
     */
    public int delete(RdeModelAntiFraud record) {
        RdeModelAntiFraud agent = new RdeModelAntiFraud();
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
        RdeModelAntiFraud agent = new RdeModelAntiFraud();
        agent.setId(id);
        agent.setDataStatus(Byte.valueOf("1"));
        return mapper.updateById(agent);
    }

    /**
     * 查询modelId
     */
    public RdeModelAntiFraud selectModelId(RdeModelAntiFraudVO record){

        return mapper.select(record.getVersionControl(),record.getProjectCode(),record.getBusinessCode(),record.getName(),record.getDeptId(),0);
    }


    public void updateState(RdeUpdateStateEntryVO rdeUpdateStateEntryVO, HttpServletRequest request) {
        logger.info("规则/分类模型状态修改入参: " + JSON.toJSONString(rdeUpdateStateEntryVO));
        rdeUpdateStateEntryVO.setDeptId(SecurityUtils.getLoginUser().getSysUser().getDeptId().intValue());
        Integer classify = rdeUpdateStateEntryVO.getClassify();
        if (classify == 1) {
            LambdaQueryWrapper<RdeModelAntiFraud> wrapperAntiFraud = Wrappers.lambdaQuery();
            wrapperAntiFraud.eq(RdeModelAntiFraud::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                    .eq(RdeModelAntiFraud::getId,rdeUpdateStateEntryVO.getId())
                    .eq(RdeModelAntiFraud::getDataStatus,0);
            RdeModelAntiFraud rdeModelAntiFraud = mapper.selectOne(wrapperAntiFraud);
            if (rdeModelAntiFraud != null){
                rdeUpdateStateEntryVO.setTableName("rde_model_anti_fraud");
                mapper.updateStatus(rdeUpdateStateEntryVO);
            }else {
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setButtonState(rdeUpdateStateEntryVO.getStatus());
                ruleRecordReuseMapper.update(ruleRecordReuse, new LambdaQueryWrapper<RuleRecordReuse>().eq(RuleRecordReuse::getParentCardId, rdeUpdateStateEntryVO.getId())
                        .eq(RuleRecordReuse::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                        .eq(RuleRecordReuse::getBuildProjectCode,rdeUpdateStateEntryVO.getProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,rdeUpdateStateEntryVO.getBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,rdeUpdateStateEntryVO.getRuleCode())
                        .eq(RuleRecordReuse::getVersionControl,rdeUpdateStateEntryVO.getVersionControl())
                        .eq(RuleRecordReuse::getDataStatus,0));
            }
        } else if (classify == 2) {
            LambdaQueryWrapper<RdeModelAntiFraudRuleGroup> wrapperRuleGroup = Wrappers.lambdaQuery();
            wrapperRuleGroup.eq(RdeModelAntiFraudRuleGroup::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                    .eq(RdeModelAntiFraudRuleGroup::getId,rdeUpdateStateEntryVO.getId())
                    .eq(RdeModelAntiFraudRuleGroup::getDataStatus,0);
            RdeModelAntiFraudRuleGroup rdeModelAntiFraudRuleGroup = ruleGroupMapper.selectOne(wrapperRuleGroup);
            if (rdeModelAntiFraudRuleGroup != null){
                rdeUpdateStateEntryVO.setTableName("rde_model_anti_fraud_rule_group");
                mapper.updateStatus(rdeUpdateStateEntryVO);
            }else {
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setButtonState(rdeUpdateStateEntryVO.getStatus());
                ruleRecordReuseMapper.update(ruleRecordReuse, new LambdaQueryWrapper<RuleRecordReuse>().eq(RuleRecordReuse::getGroupId, rdeUpdateStateEntryVO.getId())
                        .eq(RuleRecordReuse::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                        .eq(RuleRecordReuse::getBuildProjectCode,rdeUpdateStateEntryVO.getProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,rdeUpdateStateEntryVO.getBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,rdeUpdateStateEntryVO.getRuleCode())
                        .eq(RuleRecordReuse::getVersionControl,rdeUpdateStateEntryVO.getVersionControl())
                        .eq(RuleRecordReuse::getDataStatus,0));
            }
        } else if (classify == 3) {
            LambdaQueryWrapper<RdeModelAntiFraudRuleRecord> wrapperRuleRecord = Wrappers.lambdaQuery();
            wrapperRuleRecord.eq(RdeModelAntiFraudRuleRecord::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                    .eq(RdeModelAntiFraudRuleRecord::getId,rdeUpdateStateEntryVO.getId())
                    .eq(RdeModelAntiFraudRuleRecord::getDataStatus,0);
            RdeModelAntiFraudRuleRecord rdeModelAntiFraudRuleRecord = ruleRecordMapper.selectOne(wrapperRuleRecord);
            if(rdeModelAntiFraudRuleRecord != null){
                rdeUpdateStateEntryVO.setTableName("rde_model_anti_fraud_rule_record");
                mapper.updateStatus(rdeUpdateStateEntryVO);
            }else {
                RuleRecordReuse ruleRecordReuse = new RuleRecordReuse();
                ruleRecordReuse.setButtonState(rdeUpdateStateEntryVO.getStatus());
                ruleRecordReuseMapper.update(ruleRecordReuse, new LambdaQueryWrapper<RuleRecordReuse>().eq(RuleRecordReuse::getRuleId, rdeUpdateStateEntryVO.getId())
                        .eq(RuleRecordReuse::getDeptId,rdeUpdateStateEntryVO.getDeptId())
                        .eq(RuleRecordReuse::getBuildProjectCode,rdeUpdateStateEntryVO.getProjectCode())
                        .eq(RuleRecordReuse::getBuildBusinessCode,rdeUpdateStateEntryVO.getBusinessCode())
                        .eq(RuleRecordReuse::getBuildRuleCode,rdeUpdateStateEntryVO.getRuleCode())
                        .eq(RuleRecordReuse::getVersionControl,rdeUpdateStateEntryVO.getVersionControl())
                        .eq(RuleRecordReuse::getDataStatus,0));
            }

        } else if (classify == 4) {
            rdeUpdateStateEntryVO.setTableName("rde_model_decision_code_level");
            mapper.updateStatus(rdeUpdateStateEntryVO);
        }
    }

}
