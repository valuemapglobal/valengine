package com.value.decision.process.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.configure.ModelTaskRecordProperties;
import com.risksmart.common.core.constant.ModelConstants;
import com.risksmart.common.core.constant.SecurityConstants;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestDTO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestVO;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.model.ProcessPolicy;
import com.value.decision.process.service.IProcessNodeService;
import com.value.decision.process.service.IProcessPolicyService;
import com.value.decision.process.service.IProcessPolicyTaskService;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.mapper.RdeModelAntiFraudVersionMapper;
import com.value.decision.version.mapper.ScoreCardRecordVersionMapper;
import com.value.decision.common.utils.security.SecurityUtils;
import com.risksmart.system.domain.SysUser;
import com.value.decision.common.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程策略表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@Slf4j
@RestController
@RequestMapping("/process-policy")
public class ProcessPolicyController {

    @Autowired
    private IProcessPolicyService processPolicyService;

    @Autowired
    private IProcessNodeService processNodeService;

    @Autowired
    private ScoreCardRecordSnapshotMapper scoreCardRecordMapper;
    @Autowired
    private ScoreCardRecordVersionMapper scoreCardRecordVersionMapper;
    @Autowired
    private RateCardRecordSnapshotMapper rateCardRecordMapper;
    @Autowired
    private QuotaCardRecordSnapshotMapper quotaCardRecordMapper;
    @Autowired
    private PriceCardRecordSnapshotMapper priceCardRecordMapper;
    @Autowired
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudMapper;
    @Autowired
    private RdeModelAntiFraudVersionMapper rdeModelAntiFraudVersionMapper;
    @Autowired
    private IProcessPolicyTaskService processPolicyTaskService;
    @Autowired
    private ScoreCardReuseSnapshotMapper scoreCardReuseSnapshotMapper;

    @Autowired
    private RuleRecordReuseSnapshotMapper ruleRecordReuseSnapshotMapper;
    @Autowired
    private RuoYiService ruoYiService;
    @Autowired
    private ModelTaskRecordProperties properties;


    @GetMapping("/select")
    public AjaxResult select() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }

        LambdaQueryWrapper<ProcessPolicy> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProcessPolicy::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ProcessPolicy::getDataStatus,0)
                .eq(ProcessPolicy::getUseIf,ModelConstants.USE);
        List<ProcessPolicy> list = processPolicyService.list(wrapper);

        if (CollectionUtils.isNotEmpty(list)){
            list = list.stream().sorted(Comparator.comparing(ProcessPolicy::getCreateTime).reversed()).collect(Collectors.toList());
        }


        return AjaxResult.success(list);
    }

    @Log(title = "流程策略新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    @Transactional
    public AjaxResult submit(@Validated @RequestBody ProcessPolicy processPolicy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        LambdaQueryWrapper<ProcessPolicy> eq = new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ProcessPolicy::getProcessStrategy, processPolicy.getProcessStrategy());
        if (processPolicy.getId() != null) {
            eq.ne(ProcessPolicy::getId, processPolicy.getId());
        }
        if (processPolicyService.count(eq) > 0) {
            return AjaxResult.error("策略名称已存在");
        }

        //新增
        processPolicy.setUseIf(0);
        processPolicy.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        processPolicy.setUserId(loginUser.getUserid().intValue());
        processPolicy.setCreateTime(LocalDateTime.now());
        processPolicy.setDataStatus(0);
        processPolicyService.save(processPolicy);
        if (processPolicy.getMapList() != null) {
            AtomicInteger i = new AtomicInteger(0);
            processPolicy.getMapList().forEach(map -> {
                ProcessNode processNode = new ProcessNode();
                processNode.setProcessStrategyId(processPolicy.getId());
                processNode.setRuleCode(map.get("ruleCode").toString());
                processNode.setRuleName(map.get("ruleName").toString());
                processNode.setModuleId(Integer.valueOf(map.get("moduleId").toString()));
                if (map.get("approvalUserId") !=null) {
                    processNode.setApprovalUserId(StrUtil.isNotBlank(map.get("approvalUserId").toString()) ? Integer.valueOf(map.get("approvalUserId").toString()) : null);
                }
                if (map.get("approvalName") !=null) {
                    processNode.setApprovalName(StrUtil.isNotBlank(map.get("approvalName").toString()) ? map.get("approvalName").toString() : null);
                }
                processNode.setUserId(loginUser.getUserid().intValue());
                processNode.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                processNode.setCreateTime(LocalDateTime.now());
                processNode.setDataStatus(0);
                processNode.setNodeId(i.incrementAndGet());
                processNodeService.save(processNode);
            });
        }

        return AjaxResult.success();
    }

    @Log(title = "流程策略编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    @Transactional
    public AjaxResult update(@Validated @RequestBody ProcessPolicy processPolicy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        LambdaQueryWrapper<ProcessPolicy> eq = new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ProcessPolicy::getProcessStrategy, processPolicy.getProcessStrategy());
        if (processPolicy.getId() != null) {
            eq.ne(ProcessPolicy::getId, processPolicy.getId());
        }
        if (processPolicyService.count(eq) > 0) {
            return AjaxResult.error("策略名称已存在");
        }

        //编辑
        ProcessPolicy policy = processPolicyService.getById(processPolicy.getId());
        if (policy != null) {
            if (policy.getUseIf() == 1) {
                return AjaxResult.error("请先禁用流程策略模型，再进行编辑");
            }
            processPolicy.setUpdateTime(LocalDateTime.now());
            processPolicyService.updateById(processPolicy);
            processNodeService.remove(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId, processPolicy.getId()));
            if (processPolicy.getMapList() != null) {
                AtomicInteger i = new AtomicInteger(0);
                processPolicy.getMapList().forEach(map -> {
                    ProcessNode processNode = new ProcessNode();
                    processNode.setProcessStrategyId(processPolicy.getId());
                    processNode.setRuleCode(map.get("ruleCode").toString());
                    if (map.get("approvalUserId") !=null) {
                        processNode.setApprovalUserId(StrUtil.isNotBlank(map.get("approvalUserId").toString()) ? Integer.valueOf(map.get("approvalUserId").toString()) : null);
                    }
                    if (map.get("approvalName") !=null) {
                        processNode.setApprovalName(StrUtil.isNotBlank(map.get("approvalName").toString()) ? map.get("approvalName").toString() : null);
                    }
                    processNode.setRuleName(map.get("ruleName").toString());
                    processNode.setModuleId(Integer.valueOf(map.get("moduleId").toString()));
                    processNode.setUpdateTime(LocalDateTime.now());
                    processNode.setUserId(loginUser.getUserid().intValue());
                    processNode.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                    processNode.setCreateTime(LocalDateTime.now());
                    processNode.setUpdateTime(LocalDateTime.now());
                    processNode.setDataStatus(0);
                    processNode.setNodeId(i.incrementAndGet());
                    processNodeService.save(processNode);
                });
            }
        }

        return AjaxResult.success();
    }

    /**
     * 流程策略重名判断
     *
     * @param processPolicy
     * @return
     */
    @RequestMapping("checkName")
    public AjaxResult checkName(@RequestBody ProcessPolicy processPolicy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        if (StrUtil.isBlank(processPolicy.getProcessStrategy())) {
            return AjaxResult.error("参数缺失");
        }
        LambdaQueryWrapper<ProcessPolicy> eq = new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getDeptId, loginUser.getSysUser().getDeptId())
                .eq(ProcessPolicy::getProcessStrategy, processPolicy.getProcessStrategy());
        if (processPolicy.getId() != null) {
            eq.ne(ProcessPolicy::getId, processPolicy.getId());
        }
        if (processPolicyService.count(eq) > 0) {
            return AjaxResult.error("策略名称已存在");
        }
        return AjaxResult.success();
    }

    @Log(title = "流程策略更新状态", businessType = BusinessType.UPDATE)
    //更新流程按钮状态
    @RequestMapping("updateStatus")
    public AjaxResult updateStatus(@RequestBody ProcessPolicy processPolicy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        if (processPolicy.getId() == null || processPolicy.getUseIf() == null) {
            return AjaxResult.error("参数缺失");
        }
        ProcessPolicy processPolicy1 = new ProcessPolicy();
        processPolicy1.setUseIf(processPolicy.getUseIf());
        processPolicyService.update(processPolicy1, new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getId, processPolicy.getId()));
        return AjaxResult.success();
    }

    @Log(title = "流程策略删除", businessType = BusinessType.DELETE)
    @RequestMapping("delete")
    public AjaxResult delete(@RequestBody ProcessPolicy processPolicy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        ProcessPolicy policy = processPolicyService.getById(processPolicy.getId());
        if (policy != null && policy.getUseIf() == 1) {
            return AjaxResult.error("请先禁用流程策略模型，再进行删除");
        }
        // 检查是否存在未完成的任务
        int approvalTaskCount = processPolicyTaskService.getApprovalTaskCount(policy.getId());
        if (approvalTaskCount != 0) {
            return AjaxResult.error("存在未流转完的审批任务，无法删除");
        }
        processPolicyService.removeById(processPolicy.getId());
        return AjaxResult.success();
    }


    @RequestMapping("list")
    public AjaxResult list(@RequestBody ProcessPolicy policy) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        LambdaQueryWrapper<ProcessPolicy> processPolicyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(policy.getProcessStrategy())) {
            processPolicyLambdaQueryWrapper.like(ProcessPolicy::getProcessStrategy, policy.getProcessStrategy());
        }
        if (StrUtil.isNotBlank(policy.getProductName())) {
            processPolicyLambdaQueryWrapper.eq(ProcessPolicy::getProductName, policy.getProductName());
        }
        if (StrUtil.isNotBlank(policy.getBusinessCode())) {
            processPolicyLambdaQueryWrapper.eq(ProcessPolicy::getBusinessCode, policy.getBusinessCode());
        }
        if (policy.getUseIf() != null) {
            processPolicyLambdaQueryWrapper.eq(ProcessPolicy::getUseIf, policy.getUseIf());
        }
        if(StrUtil.isNotBlank(policy.getStartTime())){
            processPolicyLambdaQueryWrapper.gt(ProcessPolicy::getCreateTime,policy.getStartTime());
        }
        if(StrUtil.isNotBlank(policy.getEndTime())){
            processPolicyLambdaQueryWrapper.lt(ProcessPolicy::getCreateTime,policy.getEndTime());
        }
        processPolicyLambdaQueryWrapper.eq(ProcessPolicy::getDeptId, loginUser.getSysUser().getDeptId());
        processPolicyLambdaQueryWrapper.orderByDesc(ProcessPolicy::getCreateTime);
        PageHelper.startPage(policy.getPageNum() != null ? policy.getPageNum() : 1, policy.getPageSize() != null ? policy.getPageSize() : 10);
        List<ProcessPolicy> list = processPolicyService.list(processPolicyLambdaQueryWrapper);
        list.stream().forEach(x->{
            x.setMapList(processNodeService.listMaps(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId,x.getId()).orderByAsc(ProcessNode::getNodeId)));
        });
        return AjaxResult.success(new PageInfo<>(list));
    }



    @RequestMapping("detail/{id}")
    public AjaxResult detail(@PathVariable("id") Integer id) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        ProcessPolicy byId = processPolicyService.getById(id);
        byId.setMapList(processNodeService.listMaps(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId,byId.getId()).orderByAsc(ProcessNode::getNodeId)));
        return AjaxResult.success(byId);
    }

    @PostMapping("selectList")
    public AjaxResult selectList() {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<ProcessPolicy> list = processPolicyService.list(loginUser.getUserid().intValue());
        return AjaxResult.success(list);
    }

    @RequestMapping("getmoduleId")
    public AjaxResult getmoduleId(@RequestBody HashMap<String, Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        List<Map<String, Object>> mapInitList = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "评分");
        hashMap.put("value", "1");
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("name", "评级");
        hashMap2.put("value", "2");
        HashMap<String, Object> hashMap3 = new HashMap<>();
        hashMap3.put("name", "额度");
        hashMap3.put("value", "3");
        HashMap<String, Object> hashMap4 = new HashMap<>();
        hashMap4.put("name", "定价");
        hashMap4.put("value", "4");
        HashMap<String, Object> hashMap5 = new HashMap<>();
        hashMap5.put("name", "规则");
        hashMap5.put("value", "5");
        HashMap<String, Object> hashMap6 = new HashMap<>();
        hashMap6.put("name", "分类");
        hashMap6.put("value", "6");
        mapInitList.add(hashMap);
        mapInitList.add(hashMap2);
        mapInitList.add(hashMap3);
        mapInitList.add(hashMap4);
        mapInitList.add(hashMap5);
        mapInitList.add(hashMap6);

        String moduleTypeLast = "";


        List<Map<String, Object>> resultMaps = new ArrayList<>();
        if (StrUtil.isBlank(moduleTypeLast)) {
            if(mapInitList.get(0).size() !=0){
                resultMaps.add(mapInitList.get(0));
            }
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("1")) {
            if(mapInitList.get(1).size() !=0){
                resultMaps.add(mapInitList.get(1));
            }
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("2")) {
            if(mapInitList.get(2).size() !=0){
                resultMaps.add(mapInitList.get(2));
            }
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("3")) {
            if(mapInitList.get(3).size() !=0){
                resultMaps.add(mapInitList.get(3));
            }
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("4")) {
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("5")) {
            if(mapInitList.get(1).size() !=0){
                resultMaps.add(mapInitList.get(1));
            }
            if(mapInitList.get(5).size() !=0){
                resultMaps.add(mapInitList.get(5));
            }
            return AjaxResult.success(resultMaps);
        } else if (moduleTypeLast.equals("6")) {
            if(mapInitList.get(1).size() !=0){
                resultMaps.add(mapInitList.get(1));
            }
            if(mapInitList.get(4).size() !=0){
                resultMaps.add(mapInitList.get(4));
            }
            return AjaxResult.success(resultMaps);
        }
        return AjaxResult.success();
    }

    @RequestMapping("getmodule")
    public AjaxResult getModule(@RequestBody HashMap<String, Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Map<String, Object>> resultMaps = new ArrayList<>();
        Integer moduleType = null;
        if(map.get("moduleType") != null && StrUtil.isNotBlank(map.get("moduleType").toString())){
            moduleType = Integer.valueOf(map.get("moduleType").toString());
        }
        Integer key = null;
        if(map.get("key") != null && StrUtil.isNotBlank(map.get("key").toString())){
            key = Integer.valueOf(map.get("key").toString());
        }
        Integer bussiness = null;
        if(map.get("bussiness") != null && StrUtil.isNotBlank(map.get("bussiness").toString())){
            bussiness = Integer.valueOf(map.get("bussiness").toString());
        }
        //不出现重复策略
        List<Integer> strategyIds = null;
        if (map.get("strategyIds") !=null) {
            strategyIds = JSONArray.parseArray(JSON.toJSONString(map.get("strategyIds"))).toJavaList(Integer.class);
        }
        String  projectCode = null;
        if (map.get("projectCode")!=null) {
           projectCode = String.valueOf(map.get("projectCode"));
        }



//        1:评分 2:评级 3:额度 4:定价 5:规则 6:分类
        switch (moduleType) {
            case 1:
                maps = scoreCardRecordMapper.selectMaps(new LambdaQueryWrapper<ScoreCardRecordSnapshot>().eq(ScoreCardRecordSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(ScoreCardRecordSnapshot::getButtonState,1).eq(ScoreCardRecordSnapshot::getBusinessCode,bussiness).eq(ScoreCardRecordSnapshot::getProjectCode,projectCode));
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("score_card"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });
                //查询关联表数据
                List<ScoreCardReuseSnapshot> scoreCardReuseSnapshotList = scoreCardReuseSnapshotMapper.selectList(new LambdaQueryWrapper<ScoreCardReuseSnapshot>().eq(ScoreCardReuseSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(ScoreCardReuseSnapshot::getButtonState, 1).eq(ScoreCardReuseSnapshot::getBuildBusinessCode, bussiness).eq(ScoreCardReuseSnapshot::getBuildProjectCode, projectCode));
                scoreCardReuseSnapshotList.stream().forEach(x ->{
                    ScoreCardRecordVersion scoreCardRecordVersion = scoreCardRecordVersionMapper.selectOne(new LambdaQueryWrapper<ScoreCardRecordVersion>().eq(ScoreCardRecordVersion::getId, x.getParentCardId()).eq(ScoreCardRecordVersion::getVersionControl, x.getVersionControl()).eq(ScoreCardRecordVersion::getDataState, 0));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",scoreCardRecordVersion.getScoreCard());
                    hashMap.put("value",scoreCardRecordVersion.getId());
                    resultMaps.add(hashMap);
                });
                break;
            case 2:
                maps = rateCardRecordMapper.selectMaps(new LambdaQueryWrapper<RateCardRecordSnapshot>().eq(RateCardRecordSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(RateCardRecordSnapshot::getScoreCardId, key).eq(RateCardRecordSnapshot::getBusinessCode,bussiness));
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("rate_card"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });
                break;
            case 3:
                maps = quotaCardRecordMapper.selectMaps(new LambdaQueryWrapper<QuotaCardRecordSnapshot>().eq(QuotaCardRecordSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(QuotaCardRecordSnapshot::getRateCardId, key).eq(QuotaCardRecordSnapshot::getBusinessCode,bussiness));
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("quota_card"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });
                break;
            case 4:
                maps = priceCardRecordMapper.selectMaps(new LambdaQueryWrapper<PriceCardRecordSnapshot>().eq(PriceCardRecordSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(PriceCardRecordSnapshot::getRateCardId, key).eq(PriceCardRecordSnapshot::getBusinessCode,bussiness));
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("price_card"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });
                break;
            case 5:
                LambdaQueryWrapper<RdeModelAntiFraudSnapshot> rdeModelAntiFraudSnapshotLambdaQueryWrapper = new LambdaQueryWrapper<RdeModelAntiFraudSnapshot>().eq(RdeModelAntiFraudSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(RdeModelAntiFraudSnapshot::getRuleCode, 5).eq(RdeModelAntiFraudSnapshot::getBusinessCode, bussiness)
                        .eq(projectCode!=null,RdeModelAntiFraudSnapshot::getProjectCode,projectCode);
                if(CollUtil.isNotEmpty(strategyIds)){
                    rdeModelAntiFraudSnapshotLambdaQueryWrapper.notIn(RdeModelAntiFraudSnapshot::getId,strategyIds);
                }
                maps = rdeModelAntiFraudMapper.selectMaps(rdeModelAntiFraudSnapshotLambdaQueryWrapper);
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("name"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });

                //查询关联表数据
                List<RuleRecordReuseSnapshot> ruleRecordReuseSnapshotList = ruleRecordReuseSnapshotMapper.selectList(new LambdaQueryWrapper<RuleRecordReuseSnapshot>().eq(RuleRecordReuseSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(RuleRecordReuseSnapshot::getButtonState, 1).eq(RuleRecordReuseSnapshot::getBuildBusinessCode, bussiness).eq(RuleRecordReuseSnapshot::getBuildProjectCode, projectCode).eq(RuleRecordReuseSnapshot::getMoudleId,1).eq(RuleRecordReuseSnapshot::getBuildRuleCode,5));
                ruleRecordReuseSnapshotList.stream().forEach(x ->{
                    RdeModelAntiFraudVersion rdeModelAntiFraudVersion = rdeModelAntiFraudVersionMapper.selectOne(new LambdaQueryWrapper<RdeModelAntiFraudVersion>().eq(RdeModelAntiFraudVersion::getId, x.getParentCardId()).eq(RdeModelAntiFraudVersion::getVersionControl, x.getVersionControl()).eq(RdeModelAntiFraudVersion::getDataStatus, 0));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",rdeModelAntiFraudVersion.getName());
                    hashMap.put("value",rdeModelAntiFraudVersion.getId());
                    resultMaps.add(hashMap);
                });
                break;
            case 6:
                maps = rdeModelAntiFraudMapper.selectMaps(new LambdaQueryWrapper<RdeModelAntiFraudSnapshot>().eq(RdeModelAntiFraudSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(RdeModelAntiFraudSnapshot::getRuleCode, 6).eq(RdeModelAntiFraudSnapshot::getBusinessCode,bussiness)
                        .eq(projectCode!=null,RdeModelAntiFraudSnapshot::getProjectCode,projectCode));
                maps.forEach(x->{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",x.get("name"));
                    hashMap.put("value",x.get("id"));
                    resultMaps.add(hashMap);
                });
                //查询关联表数据
                List<RuleRecordReuseSnapshot> ruleRecordReuseSnapshotSortList = ruleRecordReuseSnapshotMapper.selectList(new LambdaQueryWrapper<RuleRecordReuseSnapshot>().eq(RuleRecordReuseSnapshot::getDeptId, loginUser.getSysUser().getDeptId())
                        .eq(RuleRecordReuseSnapshot::getButtonState, 1).eq(RuleRecordReuseSnapshot::getBuildBusinessCode, bussiness).eq(RuleRecordReuseSnapshot::getBuildProjectCode, projectCode).eq(RuleRecordReuseSnapshot::getMoudleId,1).eq(RuleRecordReuseSnapshot::getBuildRuleCode,6));
                ruleRecordReuseSnapshotSortList.stream().forEach(x ->{
                    RdeModelAntiFraudVersion rdeModelAntiFraudVersion = rdeModelAntiFraudVersionMapper.selectOne(new LambdaQueryWrapper<RdeModelAntiFraudVersion>().eq(RdeModelAntiFraudVersion::getId, x.getParentCardId()).eq(RdeModelAntiFraudVersion::getVersionControl, x.getVersionControl()).eq(RdeModelAntiFraudVersion::getDataStatus, 0));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",rdeModelAntiFraudVersion.getName());
                    hashMap.put("value",rdeModelAntiFraudVersion.getId());
                    resultMaps.add(hashMap);
                });
                break;
        }
        return AjaxResult.success(resultMaps);
    }

    @Resource
    private ScoreIndexRuleMapper scoreIndexRuleMapper;

    @RequestMapping("test")
    public void test() throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<>();
        map.put("ckey", "32ff635280c6470693eb2b8dd51266e3");
        String cacheRes = HttpUtil.get("http://124.222.87.164:12324/company/all.json?ckey=32ff635280c6470693eb2b8dd51266e3");
        JSONObject resData = JSONObject.parseObject(cacheRes).getJSONObject("data");
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = resData.getJSONObject("companyModules").getJSONArray("commonTaxpayer");
        ArrayList<String> strings = new ArrayList<>();

        ScoreIndexRule scoreIndexRule = scoreIndexRuleMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRule>().eq(ScoreIndexRule::getId, "42"));
        strings.add(scoreIndexRule.getTermRule());
        jsonObject.put("strList", strings);
        jsonObject.put("JsonObject", resData);
        System.out.println(jsonObject.toString());
        String encode = URLEncoder.encode(jsonObject.toString(), "UTF-8");
        data.put("data", encode);
        System.out.println(data);
        String post = HttpUtil.post("http://192.168.1.114:7092/rule/code/decisionCode", data.toString());
        System.out.println(post);
    }


    /**
     * `流程ID获取入参`
     * @param policyId
     * @return
     */
    @GetMapping("/getRequestData/{policyId}")
    public AjaxResult getRequestData(@PathVariable Integer policyId){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        if (policyId == null){
            return AjaxResult.error(ModelConstants.MODEL_PROCESS_ID);
        }
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(policyId);
        policyRequestVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        List<PolicyRequestDTO> requestDataList = processPolicyService.getRequestData(policyRequestVO);
        return AjaxResult.success(requestDataList);
    }
    /**
     * `流程ID获取入参导出excel`
     * @param policyId
     * @return
     */
    @GetMapping("/getRequestDataExcel/{policyId}")
    public void getRequestDataExcel(@PathVariable Integer policyId, HttpServletResponse response) throws IOException {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (policyId == null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ModelConstants.MODEL_PROCESS_ID);
            return;
        }
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        String fileName = "流程入参数据";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
        
        // 获取数据
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(policyId);
        policyRequestVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        List<PolicyRequestDTO> requestDataList = processPolicyService.getRequestData(policyRequestVO);
        
        // 创建Excel并写入数据
        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            writer.renameSheet("流程入参数据");
            
            // 创建表头样式
            CellStyle headStyle = createHeaderStyle(writer);
            
            // 写入第一行（nameZh）
            writer.setRowHeight(0, 25);
            for (int i = 0; i < requestDataList.size(); i++) {
                writer.getOrCreateCell(i, 0).setCellValue(requestDataList.get(i).getNameZh());
                writer.setStyle(headStyle, i, 0);
                // 设置列宽
                writer.setColumnWidth(i, 20);
            }
            
            // 写入第二行（name）
            CellStyle defaultStyle = writer.createCellStyle();
            defaultStyle.setAlignment(HorizontalAlignment.CENTER);
            defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            for (int i = 0; i < requestDataList.size(); i++) {
                writer.getOrCreateCell(i, 1).setCellValue(requestDataList.get(i).getName());
                writer.setStyle(defaultStyle, i, 1);
            }
            
            // 输出到响应流
            writer.flush(response.getOutputStream());
        } catch (Exception e) {
            log.error("生成Excel失败", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成Excel失败");
        }
    }
    
    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(ExcelWriter writer) {
        CellStyle style = writer.createCellStyle();
        Font font = writer.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * `流程ID获取入参`
     * @param policyId
     * @return
     */
    @GetMapping("/getRequestDataBussiness/{policyId}/{userIdentification}")
    public AjaxResult getRequestData2(@PathVariable Integer policyId,@PathVariable String userIdentification){
        //解密得到userId
        String aesKey = properties.getAesKey();
        byte[] key = HexUtil.decodeHex(aesKey);
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        String userIdStr = aes.decryptStr(userIdentification, CharsetUtil.CHARSET_UTF_8);
        log.info("解密得到用户ID：{}",userIdStr);
        Long userId = Long.valueOf(userIdStr);
        //通过userId获取用户信息
        SysUser userInfo = ruoYiService.getUserById(userId);
        if (userInfo==null){
            return AjaxResult.error(String.format("未找到用户%s的信息",userIdStr));
        }

        if (policyId == null){
            return AjaxResult.error(ModelConstants.MODEL_PROCESS_ID);
        }
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(policyId);
        policyRequestVO.setDeptId(userInfo.getDeptId().intValue());
        List<PolicyRequestDTO> requestDataList = processPolicyService.getRequestData(policyRequestVO);
        return AjaxResult.success(requestDataList);
    }

}
