package com.value.decision.process.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.utils.CommonUtil;
import com.value.decision.common.utils.IDCardUtil;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.process.common.RandomNumberUtil;
import com.value.decision.process.common.RuleFunction;
import com.value.decision.process.dto.GetInterfaceInputParameterDTO;
import com.value.decision.process.model.*;
import com.value.decision.process.service.*;
import com.value.decision.process.service.feign.FeignDataMiddleStationService;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.value.decision.snapshot.mapper.RdeModelDecisionCodeLevelSnapshotMapper;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 流程策略任务表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@RestController
@RequestMapping("/process-policy-task")
public class ProcessPolicyTaskController {

    @Autowired
    private IProcessPolicyTaskService iProcessPolicyTaskService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 额度任务回调接口
     */
    @PostMapping("/limitTask")
    public AjaxResult limitTask(ProcesPolicyVO procesPolicyVO) {

        //额度模块
        procesPolicyVO.setModuleId(3);
        ProcesPolicyDTO procesPolicyDTO = iProcessPolicyTaskService.limitTask(procesPolicyVO);
        return AjaxResult.success(procesPolicyDTO);
    }

    @Autowired
    private IProcessPolicyTaskService processPolicyTaskService;

    @Autowired
    private IProcessNodeService processNodeServicel;

    @Autowired
    private RuleFunction ruleFunction;

    @Autowired
    private IProcessNodeResultService processNodeResultService;

    @Resource
    private IProcessPolicyService processPolicyService;


    @RequestMapping("startTaskWithOutToken")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult startTaskWithOutToken(@Validated @RequestBody ProcessPolicyTask processPolicyTask) throws Exception {
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        if (loginUser == null) {
//            return AjaxResult.error("用户未登录");
//        }
        ProcessPolicy one = processPolicyService.getOne(new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getId, processPolicyTask.getProcessStrategyId()));
        if (one == null ) {
            return AjaxResult.error("流程策略不存在");
        }
        if (one.getUseIf()==0) {
            return AjaxResult.error("流程策略未开启");
        }
        //根据节点id  (任务启动时 默认为 1 )  流程id 获取节点信息
        ProcessNode processNode = processNodeServicel.getOne(new LambdaQueryWrapper<ProcessNode>()
                .eq(ProcessNode::getNodeId, processPolicyTask.getNodeId())
                .eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId())
        );
        if (processNode == null) {
            return AjaxResult.error("任务节点不存在");
        }


        long count = processPolicyTaskService.count(new LambdaQueryWrapper<ProcessPolicyTask>().eq(ProcessPolicyTask::getTaskNumber, processPolicyTask.getTaskNumber()));
        if(count != 0){
            return AjaxResult.success("任务编号不可重复");
        }

        return startRuleWithOutToken(processPolicyTask);
    }

    private AjaxResult startRuleWithOutToken(ProcessPolicyTask processPolicyTask){
        Object savePointSon = null;

        //获取流程节点
        try {
            savePointSon =  TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            processPolicyTask.setCreateTime(LocalDateTime.now());
            processPolicyTask.setDeptId(processPolicyTask.getDeptId());
            processPolicyTask.setDeptName(processPolicyTask.getDeptName());
            processPolicyTask.setUserId(processPolicyTask.getUserId());
            processPolicyTaskService.save(processPolicyTask);
            List<ProcessNode> list = processNodeServicel.list(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId()));
            for (int i = processPolicyTask.getNodeId() - 1; i < list.size(); i++) {
                //进行模块任务
                AjaxResult ajaxResult = ruleFunction.startRule(list.get(i), processPolicyTask);

                ProcessNodeResult processNodeResult = new ProcessNodeResult();
                processNodeResult.setProcessId(processPolicyTask.getProcessStrategyId());
                processNodeResult.setProcessNodeId(list.get(i).getId());
                processNodeResult.setCreateTime(LocalDateTime.now());
                processNodeResult.setDeptId(processPolicyTask.getDeptId());
                processNodeResult.setUserId(processPolicyTask.getUserId());
                processNodeResult.setRuleCode(list.get(i).getRuleCode());
                processNodeResult.setApprovalUserId(list.get(i).getApprovalUserId());
                processNodeResult.setApprovalName(list.get(i).getApprovalName());
                processNodeResult.setNodeId(list.get(i).getNodeId());
                processNodeResult.setRuleName(list.get(i).getRuleName());
                processNodeResult.setModuleId(list.get(i).getModuleId());
                processNodeResult.setTaskId(processPolicyTask.getId());
                //任务结果存入 流程策略节点结果表
                if (ajaxResult.get("code").toString().equals("200")) {
                    processNodeResult.setDetail(ajaxResult.get("data").toString());
                }
                processNodeResult.setReturnData(ajaxResult.toString());
                //当前节点存在审批人 直接返还
                if (list.get(i).getApprovalUserId() != null && list.get(i).getApprovalName() != null) {
                    processNodeResult.setApprovalStatus(0);
                    processNodeResult.setResult("待审核");
                    processNodeResultService.save(processNodeResult);

                    return AjaxResult.success("待审核");
                } else {
                    processNodeResult.setApprovalStatus(-1);
                    processNodeResult.setResult("已通过");
                    processNodeResultService.save(processNodeResult);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            e.printStackTrace();
            logger.info("任务启动接口异常：" + e);
            //异常推送飞书消息
            CommonUtil.sendBotMessage("任务启动接口异常：" + e);
            return AjaxResult.error("任务启动失败，请联系管理员");
        }
        return AjaxResult.success();
    }

    @RequestMapping("startTask")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult startTask(@Validated @RequestBody ProcessPolicyTask processPolicyTask) throws Exception {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        ProcessPolicy one = processPolicyService.getOne(new LambdaQueryWrapper<ProcessPolicy>().eq(ProcessPolicy::getId, processPolicyTask.getProcessStrategyId()));
        if (one == null ) {
            return AjaxResult.error("流程策略不存在");
        }
        if (one.getUseIf()==0) {
            return AjaxResult.error("流程策略未开启");
        }
        //根据节点id  (任务启动时 默认为 1 )  流程id 获取节点信息
        ProcessNode processNode = processNodeServicel.getOne(new LambdaQueryWrapper<ProcessNode>()
                .eq(ProcessNode::getNodeId, processPolicyTask.getNodeId())
                .eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId())
        );
        if (processNode == null) {
            return AjaxResult.error("任务节点不存在");
        }


        long count = processPolicyTaskService.count(new LambdaQueryWrapper<ProcessPolicyTask>().eq(ProcessPolicyTask::getTaskNumber, processPolicyTask.getTaskNumber()));
        if(count != 0){
            return AjaxResult.success("任务编号不可重复");
        }

        return startRule(processPolicyTask,loginUser);
    }

    private AjaxResult startRule(ProcessPolicyTask processPolicyTask,LoginUser loginUser){
        Object savePointSon = null;

        //获取流程节点
        try {
            savePointSon =  TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            processPolicyTask.setCreateTime(LocalDateTime.now());
            processPolicyTask.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            processPolicyTask.setDeptName(loginUser.getSysUser().getDept().getDeptName());
            processPolicyTask.setUserId(loginUser.getSysUser().getUserId().intValue());
            processPolicyTaskService.save(processPolicyTask);
            List<ProcessNode> list = processNodeServicel.list(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId()));
            for (int i = processPolicyTask.getNodeId() - 1; i < list.size(); i++) {
                //进行模块任务
                AjaxResult ajaxResult = ruleFunction.startRule(list.get(i), processPolicyTask);
                ProcessNodeResult processNodeResult = new ProcessNodeResult();
                processNodeResult.setProcessId(processPolicyTask.getProcessStrategyId());
                processNodeResult.setProcessNodeId(list.get(i).getId());
                processNodeResult.setCreateTime(LocalDateTime.now());
                processNodeResult.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                processNodeResult.setUserId(loginUser.getSysUser().getUserId().intValue());
                processNodeResult.setRuleCode(list.get(i).getRuleCode());
                processNodeResult.setApprovalUserId(list.get(i).getApprovalUserId());
                processNodeResult.setApprovalName(list.get(i).getApprovalName());
                processNodeResult.setNodeId(list.get(i).getNodeId());
                processNodeResult.setRuleName(list.get(i).getRuleName());
                processNodeResult.setModuleId(list.get(i).getModuleId());
                processNodeResult.setTaskId(processPolicyTask.getId());
                //任务结果存入 流程策略节点结果表
                if (ajaxResult.get("code").toString().equals("200")) {
                    processNodeResult.setDetail(ajaxResult.get("data").toString());
                }
                processNodeResult.setReturnData(ajaxResult.toString());
                //当前节点存在审批人 直接返还
                if (list.get(i).getApprovalUserId() != null && list.get(i).getApprovalName() != null) {
                    processNodeResult.setApprovalStatus(0);
                    processNodeResult.setResult("待审核");
                    processNodeResultService.save(processNodeResult);

                    return AjaxResult.success("待审核");
                } else {
                    processNodeResult.setApprovalStatus(-1);
                    processNodeResult.setResult("已通过");
                    processNodeResultService.save(processNodeResult);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePointSon);
            e.printStackTrace();
            logger.info("任务启动接口异常：" + e);
            //异常推送飞书消息
            CommonUtil.sendBotMessage("任务启动接口异常：" + e);
            return AjaxResult.error("任务启动失败，请联系管理员");
        }
        return AjaxResult.success();
    }


    //获取待审批状态下的 任务  审批状态 -1 无需审核 0 待审核 1 审核通过 2审核拒绝
    @RequestMapping("getTask")
    public AjaxResult getTask(@RequestBody Map<String, Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        //申请用户
        String approvalUserName = (String) map.get("approvalUserName");
        //金融产品
        String product = (String) map.get("product");
        //业务场景
        String businessCode = (String) map.get("businessCode");
        //申请时间 起
        String startTime = (String) map.get("startTime");
        //申请时间 止
        String endTime = (String) map.get("endTime");
        PageHelper.startPage(map.get("pageNum") == null ? 1 : (int) map.get("pageNum"), map.get("pageSize") == null ? 10 : (int) map.get("pageSize"));
        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(processPolicyTaskService.getTask(approvalUserName, product, businessCode, startTime, endTime, 0, loginUser.getSysUser().getUserId().intValue()));
        return AjaxResult.success(mapPageInfo);

    }

    //审批
    @RequestMapping("approval")
    @Transactional
    public AjaxResult approval(@RequestBody HashMap<String, Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        // 审批状态 -1 无需审核 0 待审核 1 审核通过 2审核拒绝
        String approvalStatus = map.get("approvalStatus").toString();
        //审批意见
        String approvalRemark = map.get("approvalRemark").toString();
        String nodeId = map.get("nodeId").toString();
        String processTask = map.get("processTask").toString();
        String id = map.get("id").toString();
        if (StrUtil.isBlank(approvalStatus) || StrUtil.isBlank(id) || StrUtil.isBlank(nodeId) || StrUtil.isBlank(processTask) ) {
            return AjaxResult.error("参数不可为空");
        }
        if (approvalStatus.equals("2") && StrUtil.isBlank(approvalRemark)) {
            return AjaxResult.error("审批结果拒绝时，审批意见不可为空");
        }
        LambdaQueryWrapper<ProcessNodeResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessNodeResult::getId, id);
        ProcessNodeResult processNodeResultS = new ProcessNodeResult();
        processNodeResultS.setApprovalStatus(Integer.valueOf(approvalStatus));
        processNodeResultS.setOpinion(approvalRemark);
        processNodeResultS.setApprovalTime(LocalDateTime.now());
        if (Integer.parseInt(approvalStatus) == 1) {
            processNodeResultS.setResult( "已通过" );
            processNodeResultService.update(processNodeResultS, wrapper);
        }else {
            processNodeResultS.setResult( "已拒绝" );
            processNodeResultService.update(processNodeResultS, wrapper);
            return AjaxResult.success();
        }

        ProcessPolicyTask processPolicyTask = processPolicyTaskService.getOne(new LambdaQueryWrapper<ProcessPolicyTask>().eq(ProcessPolicyTask::getId, processTask));
        //根据节点id  (任务启动时 默认为 1 )  流程id 获取节点信息
        ProcessNode processNode = processNodeServicel.getOne(new LambdaQueryWrapper<ProcessNode>()
                .eq(ProcessNode::getNodeId, nodeId)
                .eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId())
        );
        //当没节点时 表明任务已执行完毕
        if (processNode == null) {
            return AjaxResult.success();
        }

        //获取流程节点
        try {
            List<ProcessNode> list = processNodeServicel.list(new LambdaQueryWrapper<ProcessNode>().eq(ProcessNode::getProcessStrategyId, processPolicyTask.getProcessStrategyId())
                    .orderByAsc(ProcessNode::getNodeId));
            for (int i = Integer.parseInt(nodeId) - 1; i < list.size(); i++) {
                //进行模块任务
                AjaxResult ajaxResult = ruleFunction.startRule(list.get(i), processPolicyTask);
                ProcessNodeResult processNodeResult = new ProcessNodeResult();
                processNodeResult.setProcessId(processPolicyTask.getProcessStrategyId());
                processNodeResult.setProcessNodeId(list.get(i).getId());
                processNodeResult.setCreateTime(LocalDateTime.now());
                processNodeResult.setDeptId(loginUser.getSysUser().getDeptId().intValue());
                processNodeResult.setUserId(loginUser.getSysUser().getUserId().intValue());
                processNodeResult.setRuleCode(list.get(i).getRuleCode());
                processNodeResult.setApprovalUserId(list.get(i).getApprovalUserId());
                processNodeResult.setApprovalName(list.get(i).getApprovalName());
                processNodeResult.setNodeId(list.get(i).getNodeId());
                processNodeResult.setRuleName(list.get(i).getRuleName());
                processNodeResult.setModuleId(list.get(i).getModuleId());
                processNodeResult.setTaskId(processPolicyTask.getId());
                //任务结果存入 流程策略节点结果表
                if (ajaxResult.get("code").toString().equals("200")) {
                    processNodeResult.setDetail(ajaxResult.get("data").toString());
                }
                processNodeResult.setReturnData(ajaxResult.toString());
                //当前节点存在审批人 直接返还
                if (list.get(i).getApprovalUserId() != null && list.get(i).getApprovalName() != null) {
                    processNodeResult.setApprovalStatus(0);
                    processNodeResult.setResult("待审核");
                    processNodeResultService.save(processNodeResult);
                    return AjaxResult.success("待审核");
                } else {
                    processNodeResult.setApprovalStatus(-1);
                    processNodeResult.setResult("已通过");
                    processNodeResultService.save(processNodeResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("审批启动接口异常：" + e);
            //异常推送飞书消息
            CommonUtil.sendBotMessage("审批启动接口异常：" + e);
        }

        return AjaxResult.success();
    }


    //获取任务审批详情接口
    @RequestMapping("getTaskDetail")
    public AjaxResult getTaskDetail(@RequestBody Map<String, Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        HashMap<String, Object> resultMap = new HashMap<>();
        //任务编号
        String id = map.get("id").toString();
        //根据任务编号 获取流程策略id
        ProcessPolicyTask processPolicyTask = processPolicyTaskService.getById(id);
        if(processPolicyTask == null){
            return AjaxResult.success();
        }
        ProcessPolicy policy = processPolicyService.getById(processPolicyTask.getProcessStrategyId());
        resultMap.put("processPolicy", policy);

        //获取运行结果
        resultMap.put("dataList", processPolicyTaskService.getNodeResult(processPolicyTask.getId()));
        return AjaxResult.success(resultMap);
    }

    //获取任务列表接口
    @RequestMapping("getTaskList")
    public AjaxResult getTaskList(@RequestBody Map<String, Object> map){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        //申请用户
        String approvalUserName = (String) map.get("approvalUserName");
        //身份证号码
        String idNumber = (String) map.get("idNumber");
        //金融产品
        String product = (String) map.get("product");
        //业务场景
        String businessCode = (String) map.get("businessCode");
        //申请时间 起
        String startTime = (String) map.get("startTime");
        //申请时间 止
        String endTime = (String) map.get("endTime");
        //申请时间 起
        String approvalStatus = (String) map.get("approvalStatus");
        String orderNo = (String) map.get("orderNo");
        String processStrategy = (String) map.get("processStrategy");
        String ruleName = (String) map.get("ruleName");
        String status = (String) map.get("status");
        String customerName = (String) map.get("customerName");
        String evaluationResult = (String) map.get("evaluationResult");
        PageHelper.startPage(map.get("pageNum") == null ? 1 : (int) map.get("pageNum"), map.get("pageSize") == null ? 10 : (int) map.get("pageSize"));
        List<Map<String, Object>> taskList = processPolicyTaskService.getTaskList(approvalUserName, idNumber, product, businessCode
                , startTime, endTime, StrUtil.isNotBlank(approvalStatus) ? Integer.valueOf(approvalStatus) : null, orderNo, processStrategy, ruleName, status, loginUser.getSysUser().getDeptId().toString(),customerName,evaluationResult);
        for (Map<String, Object> x : taskList) {
            if (x.get("idNumber") != null) {
                x.replace("idNumber", IDCardUtil.idNumberDesensitization(x.get("idNumber").toString()));
            }
            if (x.get("applyName") != null && x.get("applyName").toString().length() > 3) {
                x.replace("applyName", IDCardUtil.phoneDesensitization(x.get("applyName").toString()));
            }
            if (x.get("applyName") != null && x.get("applyName").toString().length() <= 3) {
                x.replace("applyName", IDCardUtil.userNameDesensitization(x.get("applyName").toString()));
            }
            if (x.get("customerName") != null && x.get("customerName").toString().length() <= 3) {
                x.replace("customerName", IDCardUtil.userNameDesensitization(x.get("customerName").toString()));
            }
        }

        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(taskList);
        return AjaxResult.success(mapPageInfo);
    }

    //根据字段名称获取
    @RequestMapping("getTaskListByField")
    public AjaxResult getTaskListByField(@RequestBody Map<String, Object> map){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        String field = (String) map.get("type");
        String remark = (String) map.get("remark");
        List<String> taskNumberList = (List<String>) map.get("taskNumberList");
        return AjaxResult.success(processPolicyTaskService.getTaskListByField(field,remark,taskNumberList));
    }


}
