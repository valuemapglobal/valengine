package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.util.StringUtil;
import com.risksmart.common.core.utils.StringUtils;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import com.value.decision.common.configure.ModelTaskRecordProperties;
import com.risksmart.common.core.constant.ModelConstants;
import com.risksmart.common.core.constant.QuotaCalculationConstants;
import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.common.core.web.page.TableDataInfo;
// TODO: FeignRuoYiSystemService removed - approval flow feature disabled
import com.value.decision.model.decisionmanage.constants.RuleConstants;
import com.value.decision.model.decisionmanage.enums.FailureSceneEnum;
import com.value.decision.model.decisionmanage.enums.RiskLevelEnum;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.model.dto.EvaluationModuleDTO;
import com.value.decision.model.decisionmanage.model.dto.EvaluationReportDTO;
import com.value.decision.model.decisionmanage.model.dto.EvaluationRuleDetailDTO;
import com.value.decision.model.decisionmanage.model.dto.model.*;
import com.value.decision.model.decisionmanage.service.IFailureLogService;
import com.value.decision.model.decisionmanage.service.IModelTaskRecordService;
import com.value.decision.model.decisionmanage.service.IQuotaCardRecordService;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.process.mapper.ProcessPolicyMapper;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.model.ProcessPolicy;
import com.value.decision.process.service.feign.FeignDataMiddleStationService;
import com.value.decision.process.vo.InterfaceUser;
import com.value.decision.process.dto.FindInterfaceFieldIdInfoDTO;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.domain.RdeModelDecisionCodeLevelSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudRuleRecordSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.value.decision.snapshot.mapper.RdeModelDecisionCodeLevelSnapshotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 模型任务记录表 服务实现类
 * </p>
 *
 * @author dianne
 * @since 2024-11-18
 */
@Service
@Slf4j
public class ModelTaskRecordServiceImpl extends ServiceImpl<ModelTaskRecordMapper, ModelTaskRecord> implements IModelTaskRecordService {

    @Value("${service.interfaceManage.interfaceRequestUrl}")
    private String interfaceRequestUrl;

    @Autowired
    private ModelTaskRecordMapper modelTaskRecordMapper;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Autowired
    private ProcessPolicyMapper processPolicyMapper;

    @Autowired
    private ModelProcessDataMapper modelProcessDataMapper;

    @Autowired
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordSnapshotMapper rdeModelAntiFraudRuleRecordSnapshotMapper;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @Autowired
    private IFailureLogService failureLogService;

    @Autowired
    private ModelTaskExecutionFailureMapper modelTaskExecutionFailureMapper;

    @Autowired
    private ScoreIndexRuleSnapshotMapper scoreIndexRuleSnapshotMapper;

    @Autowired
    private ScorePrimaryIndexSnapshotMapper scorePrimaryIndexMapper;

    @Autowired
    private ScoreCardRecordSnapshotMapper scoreCardRecordSnapshotMapper;

    @Autowired
    private RuleRecordReuseMapper ruleRecordReuseMapper;

    @Autowired
    private ModelRegularDataMapper modelRegularDataMapper;

    @Autowired
    private RdeModelDecisionCodeLevelSnapshotMapper rdeModelDecisionCodeLevelSnapshotMapper;

    @Autowired
    private DataCallingMapper dataCallingMapper;

    @Autowired
    private FeignDataMiddleStationService feignDataMiddleStationService;


    @Autowired
    private RdeModelAntiFraudMapper rdeModelAntiFraudMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private AnalysisRecommendationService analysisRecommendationService;

    @Autowired
    private TotalScoreCalculationService totalScoreCalculationService;

    @Autowired
    private RateCardRecordSnapshotMapper rateCardRecordSnapshotMapper;

    @Autowired
    private RateCardRadiusSnapshotMapper rateCardRadiusSnapshotMapper;

    @Autowired
    private QuotaCardRecordSnapshotMapper quotaCardRecordSnapshotMapper;

    @Autowired
    private QuotaCardRadiusSnapshotMapper quotaCardRadiusSnapshotMapper;

    @Autowired
    private PriceCardRecordSnapshotMapper priceCardRecordSnapshotMapper;

    @Autowired
    private PriceCardRadiusSnapshotMapper priceCardRadiusSnapshotMapper;

    @Autowired
    private IQuotaCardRecordService quotaCardRecordService;

    @Autowired
    private ModelTaskRecordProperties properties;

    // TODO: Approval flow feature disabled
    // @Autowired
    // private FeignRuoYiSystemService feignRuoYiSystemService;

    /**
     * 执行流程节点处理的核心逻辑
     * @param processNodeList 流程节点列表
     * @param modelTaskRecordVO 任务记录VO
     * @param processPoliciesList 流程策略列表
     */
    private void executeProcessFlow(List<ProcessNode> processNodeList, ModelTaskRecordVO modelTaskRecordVO, List<ProcessPolicy> processPoliciesList) throws UnsupportedEncodingException {
        //根据不同策略模型的节点进行处理
        //1,为评分模型时,评分表找到全部数据 规则数据drl脚本 所有数据manage_no
        //2,为规则模型,分类模型时,规则表中找到全部数据drl脚本,所有数据manage_no
        for (ProcessNode processNode : processNodeList) {
            //数据中台请求数据组装jsonObject入参
            //组装drl脚本
            //请求决策引擎,如为不同策略模型的,则分别存入model_process_data表中,任务表查询时再进行组装
            ModelRuleDTO requestrRule = null;
            ModelTestTaskDataVO modelTestTaskDataVO = new ModelTestTaskDataVO();
            modelTestTaskDataVO.setTaskNo(modelTaskRecordVO.getTaskNo());
            modelTestTaskDataVO.setRuleCode(processNode.getModuleId());
            requestrRule = commonRuleFunctionDataNew.getRequestParameter(processNode, modelTaskRecordVO.getTaskNo(), modelTaskRecordVO.getProcessEntry(), processPoliciesList.get(0), modelTaskRecordVO.getUserIdentity());

            modelTestTaskDataVO.setStrList(requestrRule.getDrlList());
            modelTestTaskDataVO.setRuleData(requestrRule.getRequestData());
            JSONObject decisionCode = commonRuleFunctionDataNew.getDecisionCode(modelTestTaskDataVO);
            ModelProcessData modelProcessData = new ModelProcessData();
            modelProcessData.setTaskNo(modelTaskRecordVO.getTaskNo());
            modelProcessData.setParameter(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
            modelProcessData.setResponseValue(JSON.toJSONString(decisionCode));
            modelProcessData.setDeptId(modelTaskRecordVO.getDeptId());
            modelProcessData.setUserId(modelTaskRecordVO.getUserId());
            modelProcessData.setRuleCode(processNode.getModuleId());
            modelProcessData.setModelId(processNode.getRuleCode());
            modelProcessDataMapper.insert(modelProcessData);

            // 如果是评分模型(moduleId=1),则自动执行评级和额度模型
            if (processNode.getModuleId() == 1) {
                try {
                    // 执行评级模型
                    executeRateCardModel(processNode, modelTaskRecordVO, decisionCode);
                } catch (Exception e) {
                    log.error("执行评级模型失败, scoreCardId={}", processNode.getRuleCode(), e);
                }
            }
        }
    }

    /**
     * 更新任务状态
     * @param taskNo 任务编号
     * @param status 状态
     */
    private void updateTaskStatus(String taskNo, Integer status) {
        ModelTaskRecord modelTaskRecord = new ModelTaskRecord();
        modelTaskRecord.setTaskStatus(status);
        LambdaQueryWrapper<ModelTaskRecord> wrapper = Wrappers.lambdaQuery(new ModelTaskRecord());
        wrapper.eq(ModelTaskRecord::getTaskNo, taskNo)
                .eq(ModelTaskRecord::getDataStatus, 0);
        modelTaskRecordMapper.update(modelTaskRecord, wrapper);
    }

    /**
     * 流程任务启动（同步）
     * @param modelTaskRecordVO
     * @return 响应结果
     */
    @Override
    public Object processTaskInitiationSync(ModelTaskRecordVO modelTaskRecordVO) throws Exception {
        //任务初始化/生成中 模型名称多个用逗号隔开 流程策略表中查找业务场景
        LambdaQueryWrapper<ProcessNode> wrapperProcessNode = Wrappers.lambdaQuery();
        wrapperProcessNode.eq(ProcessNode::getProcessStrategyId, modelTaskRecordVO.getProcessId())
                .eq(ProcessNode::getDeptId, modelTaskRecordVO.getDeptId())
                .eq(ProcessNode::getDataStatus, 0);
        List<ProcessNode> processNodeList = processNodeMapper.selectList(wrapperProcessNode);

        // 立即查询模型名称并创建快照
        String modelNameSnapshot = getModelNameSnapshot(processNodeList);

        LambdaQueryWrapper<ProcessPolicy> wrapperProcessPolicy = Wrappers.lambdaQuery();
        wrapperProcessPolicy.eq(ProcessPolicy::getId, modelTaskRecordVO.getProcessId())
                .eq(ProcessPolicy::getUseIf, ModelConstants.USE)
                .eq(ProcessPolicy::getDeptId, modelTaskRecordVO.getDeptId());
        List<ProcessPolicy> processPoliciesList = processPolicyMapper.selectList(wrapperProcessPolicy);

        ModelTaskRecord modelTaskRecord = new ModelTaskRecord();
        modelTaskRecord.setTaskNo(modelTaskRecordVO.getTaskNo());
        modelTaskRecord.setProcessId(modelTaskRecordVO.getProcessId());
        modelTaskRecord.setProcessStrategy(modelTaskRecordVO.getProcessStrategy());
        modelTaskRecord.setModelName(modelNameSnapshot);
        modelTaskRecord.setDeptId(modelTaskRecordVO.getDeptId());
        modelTaskRecord.setResponseForm(modelTaskRecordVO.getResponseForm());
        modelTaskRecord.setProcessEntry(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
        modelTaskRecord.setTaskStatus(ModelConstants.TASK_GENERATION);
        modelTaskRecord.setApplicationUser(modelTaskRecordVO.getApplicationUser());
        modelTaskRecord.setBusinessCode(CollectionUtils.isNotEmpty(processPoliciesList) ? Integer.parseInt(processPoliciesList.get(0).getBusinessCode()) : null);
        modelTaskRecordMapper.insert(modelTaskRecord);

        try {
            //执行流程节点处理
            executeProcessFlow(processNodeList, modelTaskRecordVO, processPoliciesList);

            // 计算并保存总评分
            try {
                calculateAndSaveTotalScore(modelTaskRecordVO.getTaskNo(), modelTaskRecordVO.getDeptId());
            } catch (Exception scoreException) {
                // 分数计算失败不影响主流程，记录日志即可
                log.error("任务总评分计算失败，但不影响任务执行: taskNo={}", modelTaskRecordVO.getTaskNo(), scoreException);
            }

            //成功处理：将任务状态更新为"生成成功"
            updateTaskStatus(modelTaskRecordVO.getTaskNo(), 3);

            //根据响应形式返回相应结果
            if (modelTaskRecordVO.getResponseForm() != null && modelTaskRecordVO.getResponseForm() == 2) {
                //报告形式
                return getReportResponseForm(modelTaskRecordVO);
            } else {
                //数据形式
                return dataResponseForm(modelTaskRecordVO);
            }
        } catch (Exception e) {
            String taskNo = modelTaskRecordVO.getTaskNo();
            log.error("同步流程任务执行失败: taskNo={}", taskNo, e);

            // 同步记录失败日志，确保时序性和数据准确性
            FailureSceneEnum scene = FailureSceneEnum.inferFromException(e);
            Map<String, Object> context = new HashMap<>();
            context.put("processId", modelTaskRecordVO.getProcessId());
            failureLogService.logFailureSync(taskNo, scene, e);

            //失败处理：将任务状态更新为"生成失败"
            updateTaskStatus(taskNo, 4);
            throw e;
        }
    }

    /**
     * 流程任务启动
     * @param modelTaskRecordVO
     */
    @Override
    public void processTaskInitiation(ModelTaskRecordVO modelTaskRecordVO) throws UnsupportedEncodingException {

//        ModelTaskRecordDTO modelTaskRecordDTO = new ModelTaskRecordDTO();
        //任务初始化/生成中 模型名称多个用逗号隔开 流程策略表中查找业务场景
        LambdaQueryWrapper<ProcessNode> wrapperProcessNode = Wrappers.lambdaQuery();
        wrapperProcessNode.eq(ProcessNode::getProcessStrategyId,modelTaskRecordVO.getProcessId())
                .eq(ProcessNode::getDeptId,modelTaskRecordVO.getDeptId())
                .eq(ProcessNode::getDataStatus,0);
        List<ProcessNode> processNodeList = processNodeMapper.selectList(wrapperProcessNode);
        List<String> modelIdList = processNodeList.stream().map(ProcessNode::getRuleCode).filter(StrUtil::isNotEmpty).collect(Collectors.toList());

        // 立即查询模型名称并创建快照
        String modelNameSnapshot = getModelNameSnapshot(processNodeList);

        LambdaQueryWrapper<ProcessPolicy> wrapperProcessPolicy = Wrappers.lambdaQuery();
        wrapperProcessPolicy.eq(ProcessPolicy::getId,modelTaskRecordVO.getProcessId())
                .eq(ProcessPolicy::getUseIf,ModelConstants.USE)
                .eq(ProcessPolicy::getDeptId,modelTaskRecordVO.getDeptId());
        List<ProcessPolicy> processPoliciesList = processPolicyMapper.selectList(wrapperProcessPolicy);

        ModelTaskRecord modelTaskRecord = new ModelTaskRecord();
        modelTaskRecord.setTaskNo(modelTaskRecordVO.getTaskNo());
        modelTaskRecord.setProcessId(modelTaskRecordVO.getProcessId());
        // 快照存储流程策略名称，避免因后续策略删除导致历史记录名称丢失
        if (CollectionUtils.isNotEmpty(processPoliciesList)) {
            modelTaskRecord.setProcessStrategy(processPoliciesList.get(0).getProcessStrategy());
        } else {
            modelTaskRecord.setProcessStrategy(modelTaskRecordVO.getProcessStrategy()); // 兼容老逻辑
        }
        modelTaskRecord.setModelName(modelNameSnapshot);
        modelTaskRecord.setDeptId(modelTaskRecordVO.getDeptId());
        modelTaskRecord.setResponseForm(modelTaskRecordVO.getResponseForm());
        modelTaskRecord.setProcessEntry(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
        modelTaskRecord.setTaskStatus(ModelConstants.TASK_GENERATION);
        modelTaskRecord.setApplicationUser(modelTaskRecordVO.getApplicationUser());
        modelTaskRecord.setBusinessCode(CollectionUtils.isNotEmpty(processPoliciesList)?Integer.parseInt(processPoliciesList.get(0).getBusinessCode()):null);

        // 保存流程节点快照
        String processNodesSnapshot = JSON.toJSONString(processNodeList);
        modelTaskRecord.setProcessNodesSnapshot(processNodesSnapshot);

        modelTaskRecordMapper.insert(modelTaskRecord);

        try{
            //执行流程节点处理
            executeProcessFlow(processNodeList, modelTaskRecordVO, processPoliciesList);

            // 计算并保存总评分
            try {
                calculateAndSaveTotalScore(modelTaskRecordVO.getTaskNo(), modelTaskRecordVO.getDeptId());
            } catch (Exception scoreException) {
                // 分数计算失败不影响主流程，记录日志即可
                log.error("任务总评分计算失败，但不影响任务执行: taskNo={}", modelTaskRecordVO.getTaskNo(), scoreException);
            }

            //更新模型任务表状态为成功
            updateTaskStatus(modelTaskRecordVO.getTaskNo(), 3);

        }catch (Exception e){
            String taskNo = modelTaskRecordVO.getTaskNo();
            log.error("流程任务执行失败: taskNo={}", taskNo, e);

            // 同步记录失败日志，确保时序性和数据准确性
            FailureSceneEnum scene = FailureSceneEnum.inferFromException(e);
            Map<String, Object> context = new HashMap<>();
            context.put("batchId", modelTaskRecordVO.getBatchId());
            context.put("processId", modelTaskRecordVO.getProcessId());
            failureLogService.logFailureSync(taskNo, scene, e);

            //更新模型任务表状态为失败
            updateTaskStatus(taskNo, 4);
            throw e;
        }
    }

    /**
     * 批次任务执行(不创建任务记录,仅执行流程)
     * 用于批量导入场景,任务记录由批次服务创建
     *
     * @param modelTaskRecordVO 任务参数
     * @throws UnsupportedEncodingException 编码异常
     */
    @Override
    public void executeProcessFlowForBatch(ModelTaskRecordVO modelTaskRecordVO) throws UnsupportedEncodingException {
        // 查询流程节点
        LambdaQueryWrapper<ProcessNode> wrapperProcessNode = Wrappers.lambdaQuery();
        wrapperProcessNode.eq(ProcessNode::getProcessStrategyId, modelTaskRecordVO.getProcessId())
                .eq(ProcessNode::getDeptId, modelTaskRecordVO.getDeptId())
                .eq(ProcessNode::getDataStatus, 0);
        List<ProcessNode> processNodeList = processNodeMapper.selectList(wrapperProcessNode);

        // 查询流程策略
        LambdaQueryWrapper<ProcessPolicy> wrapperProcessPolicy = Wrappers.lambdaQuery();
        wrapperProcessPolicy.eq(ProcessPolicy::getId, modelTaskRecordVO.getProcessId())
                .eq(ProcessPolicy::getUseIf, ModelConstants.USE)
                .eq(ProcessPolicy::getDeptId, modelTaskRecordVO.getDeptId());
        List<ProcessPolicy> processPoliciesList = processPolicyMapper.selectList(wrapperProcessPolicy);

        // 直接执行流程(不创建任务记录)
        executeProcessFlow(processNodeList, modelTaskRecordVO, processPoliciesList);

        // 计算并保存总评分
        try {
            calculateAndSaveTotalScore(modelTaskRecordVO.getTaskNo(), modelTaskRecordVO.getDeptId());
        } catch (Exception scoreException) {
            // 分数计算失败不影响主流程，记录日志即可
            log.error("批量任务总评分计算失败，但不影响任务执行: taskNo={}", modelTaskRecordVO.getTaskNo(), scoreException);
        }
    }

    /**
     * 流程任务列表
     * @param modelTaskRecordVO
     * @return
     */
    public List<TaskRecordDTO> taskRecordList(ModelTaskRecordVO modelTaskRecordVO) throws ParseException {

        // 1. 数据加载: 获取基础数据列表,在SQL层面进行过滤
        LambdaQueryWrapper<ModelTaskRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelTaskRecord::getDeptId, modelTaskRecordVO.getDeptId())
                .eq(ModelTaskRecord::getDataStatus, 0)
                .likeRight(ModelTaskRecord::getTaskNo, "MTask_");

        // 如果指定了batchId,则只查询该批次下的任务
        if (modelTaskRecordVO.getBatchId() != null) {
            wrapper.eq(ModelTaskRecord::getBatchId, modelTaskRecordVO.getBatchId());
        }

        // SQL层面的过滤条件(避免内存过滤)
        if (StringUtil.isNotEmpty(modelTaskRecordVO.getTaskNo())) {
            wrapper.like(ModelTaskRecord::getTaskNo, modelTaskRecordVO.getTaskNo());
        }
        if (StringUtil.isNotEmpty(modelTaskRecordVO.getProcessStrategy())) {
            wrapper.like(ModelTaskRecord::getProcessStrategy, modelTaskRecordVO.getProcessStrategy());
        }
        if (modelTaskRecordVO.getResponseForm() != null) {
            wrapper.eq(ModelTaskRecord::getResponseForm, modelTaskRecordVO.getResponseForm());
        }
        if (modelTaskRecordVO.getTaskStatus() != null) {
            wrapper.eq(ModelTaskRecord::getTaskStatus, modelTaskRecordVO.getTaskStatus());
        }

        // 时间范围过滤(在SQL层面,可以利用索引)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (modelTaskRecordVO.getStartTime() != null) {
            Date startTime = dateFormat.parse(modelTaskRecordVO.getStartTime());
            wrapper.ge(ModelTaskRecord::getCreateTime, startTime);
        }
        if (modelTaskRecordVO.getEndTime() != null) {
            Date endTime = dateFormat.parse(modelTaskRecordVO.getEndTime());
            wrapper.le(ModelTaskRecord::getCreateTime, endTime);
        }

        // 排序在SQL层面执行
        wrapper.orderByDesc(ModelTaskRecord::getCreateTime);

        // 数据库分页(如果提供了分页参数)
        if (modelTaskRecordVO.getPageNum() != null && modelTaskRecordVO.getPageSize() != null) {
            int pageNum = modelTaskRecordVO.getPageNum();
            int pageSize = modelTaskRecordVO.getPageSize();
            int offset = (pageNum - 1) * pageSize;
            wrapper.last("LIMIT " + pageSize + " OFFSET " + offset);
        }

        List<ModelTaskRecord> modelTaskRecordList = modelTaskRecordMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(modelTaskRecordList)) {
            return Collections.emptyList();
        }
        List<TaskRecordDTO> taskRecordDTOList = JSON.parseArray(JSON.toJSONString(modelTaskRecordList), TaskRecordDTO.class);

        // 2. ID收集: 遍历列表，收集所有需要查询的ID
        Set<String> scoreRuleCodes = new HashSet<>();
        Set<String> ruleOrSortRuleCodes = new HashSet<>();
        Map<Integer, List<ProcessNode>> snapshotNodesMap = new HashMap<>(); // 存储从快照解析出的节点

        for (TaskRecordDTO task : taskRecordDTOList) {
            if (StringUtils.isNotEmpty(task.getProcessNodesSnapshot())) {
                try {
                    List<ProcessNode> processNodeList = JSON.parseArray(task.getProcessNodesSnapshot(), ProcessNode.class);
                    // 使用唯一的 taskId 作为 key，避免 processId 重复导致快照被覆盖
                    snapshotNodesMap.put(task.getId(), processNodeList);
                    for (ProcessNode node : processNodeList) {
                        if (StrUtil.isNotEmpty(node.getRuleCode())) {
                            if (node.getModuleId() == 1) scoreRuleCodes.add(node.getRuleCode());
                            else if (node.getModuleId() == 5 || node.getModuleId() == 6) ruleOrSortRuleCodes.add(node.getRuleCode());
                        }
                    }
                } catch (Exception e) {
                    // 解析失败，后续会直接使用 model_name 字段
                }
            }
        }

        // 批量查询模型名称
        Map<String, String> scoreCardNameMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(scoreRuleCodes)) {
            scoreCardNameMap = scoreCardRecordSnapshotMapper.selectList(Wrappers.lambdaQuery(ScoreCardRecordSnapshot.class)
                            .in(ScoreCardRecordSnapshot::getId, scoreRuleCodes).eq(ScoreCardRecordSnapshot::getDataState, 0))
                    .stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), ScoreCardRecordSnapshot::getScoreCard, (v1, v2) -> v1));
        }

        Map<String, String> antiFraudNameMap = Collections.emptyMap();
        if (CollectionUtils.isNotEmpty(ruleOrSortRuleCodes)) {
            antiFraudNameMap = rdeModelAntiFraudSnapshotMapper.selectList(Wrappers.lambdaQuery(RdeModelAntiFraudSnapshot.class)
                            .in(RdeModelAntiFraudSnapshot::getId, ruleOrSortRuleCodes).eq(RdeModelAntiFraudSnapshot::getDataStatus, 0))
                    .stream().collect(Collectors.toMap(a -> String.valueOf(a.getId()), RdeModelAntiFraudSnapshot::getName, (v1, v2) -> v1));
        }

        // 4. 数据组装
        // 4.1 收集所有失败任务的taskNo,用于批量查询失败原因
        List<String> failedTaskNos = taskRecordDTOList.stream()
                .filter(task -> task.getTaskStatus() != null && task.getTaskStatus() == 4)
                .map(TaskRecordDTO::getTaskNo)
                .collect(Collectors.toList());

        // 4.2 批量查询失败原因（保存完整的失败记录对象，用于兜底处理）
        Map<String, ModelTaskExecutionFailure> failureLogMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(failedTaskNos)) {
            LambdaQueryWrapper<ModelTaskExecutionFailure> failureWrapper = Wrappers.lambdaQuery();
            failureWrapper.in(ModelTaskExecutionFailure::getTaskNo, failedTaskNos)
                    .eq(ModelTaskExecutionFailure::getDataStatus, 0)
                    .orderByDesc(ModelTaskExecutionFailure::getFailureTime);
            List<ModelTaskExecutionFailure> failureLogs = modelTaskExecutionFailureMapper.selectList(failureWrapper);

            // 每个taskNo只取最新的一条失败记录
            for (ModelTaskExecutionFailure log : failureLogs) {
                failureLogMap.putIfAbsent(log.getTaskNo(), log);
            }
        }

        // 4.3 组装数据
        for (TaskRecordDTO task : taskRecordDTOList) {
            // 获取节点列表（仅从快照获取）
            List<ProcessNode> processNodeList = snapshotNodesMap.get(task.getId());

            // 如果快照不存在或为空，则直接使用任务记录中已快照的model_name字段
            if (CollectionUtils.isEmpty(processNodeList)) {
                task.setModelName(task.getModelName()); // task.getModelName() 中已经是快照名称
                task.setModelNameList(StrUtil.isNotBlank(task.getModelName()) ? Arrays.asList(task.getModelName().split(",")) : Collections.emptyList());
            } else {
                // 组装模型名称
                List<String> modelNames = new ArrayList<>();
                for (ProcessNode node : processNodeList) {
                    if (StrUtil.isEmpty(node.getRuleCode())) continue;
                    String modelName = null;
                    if (node.getModuleId() == 1) {
                        modelName = scoreCardNameMap.get(node.getRuleCode());
                    } else if (node.getModuleId() == 5 || node.getModuleId() == 6) {
                        modelName = antiFraudNameMap.get(node.getRuleCode());
                    }
                    if (modelName != null) {
                        modelNames.add(modelName);
                    }
                }
                task.setModelNameList(modelNames);
                task.setModelName(String.join(",", modelNames));
            }

            // 4.4 填充失败原因（直接使用用户友好提示）
            if (task.getTaskStatus() != null && task.getTaskStatus() == 4) {
                ModelTaskExecutionFailure failureLog = failureLogMap.get(task.getTaskNo());

                // 直接使用数据库中的 userMessage（用户友好提示）
                if (failureLog != null && failureLog.getUserMessage() != null) {
                    task.setFailureMessage(failureLog.getUserMessage());
                } else {
                    task.setFailureMessage("未记录失败原因");
                }
            }
        }

        // 5. 返回结果(过滤和排序已在SQL层面完成)
        return taskRecordDTOList;
    }

    /**
     * 数据响应形式
     * @param modelTaskRecordVO
     * @return
     */
    public ModelRuleResultDTO dataResponseForm(ModelTaskRecordVO modelTaskRecordVO) {
        ModelRuleResultDTO modelRuleResultDTO = new ModelRuleResultDTO();
        LambdaQueryWrapper<ModelProcessData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelProcessData::getTaskNo,modelTaskRecordVO.getTaskNo())
                .eq(ModelProcessData::getDataStatus,0)
                .eq(ModelProcessData::getDeptId,modelTaskRecordVO.getDeptId());
        List<ModelProcessData> modelProcessDataList = modelProcessDataMapper.selectList(wrapper);
        List<Map<String, Object>> mapScore = new ArrayList<>();
        List<Map<String, Object>> mapRule = new ArrayList<>();
        modelProcessDataList.stream().forEach(x ->{
            switch (x.getRuleCode()){
                case 1:
                    JSONObject jsonObjectScore = JSONObject.parseObject(x.getResponseValue());
                    JSONArray codeListScore = new JSONArray();
                    BigDecimal score = new BigDecimal(0);
                    if(jsonObjectScore !=null && jsonObjectScore.size() !=0){
                        //命中code集合
                        JSONObject objectJSONObject = jsonObjectScore.getJSONObject("data");
                        if(objectJSONObject !=null &&objectJSONObject.size() != 0 ){
                            codeListScore = objectJSONObject.getJSONArray("codeList");
                            //获取所有规则的上级指标卡id
                            List<Integer> primaryCardById = scoreIndexRuleSnapshotMapper.getPrimaryCardById(x.getModelId());
                            for (Integer primaryCardId : primaryCardById) {
                                //根据指标卡获取规则 并判断规则是否命中
                                List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleSnapshotMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                                        .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                                        .eq(ScoreIndexRuleSnapshot::getScordCardId, x.getModelId())
                                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                                        .eq(ScoreIndexRuleSnapshot::getDefaultRule, 0)
                                        .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                                        .orderByAsc(ScoreIndexRuleSnapshot::getLevel));
                                //标识 在该指标卡下的规则是否有命中的规则 若不存在命中的规则 取默认规则评分
                                boolean flag = false;
                                for (ScoreIndexRuleSnapshot scoreIndexRule : scoreIndexRuleSnapshots) {
                                    if (codeListScore.contains(scoreIndexRule.getIndexRule())) {
                                        flag = true;
                                        //判断是否独立生效  否 获取权重并乘评分
                                        if (scoreIndexRule.getTakeEffect() == 1) {
                                            score = score.add(BigDecimal.valueOf(scoreIndexRule.getScore()));
                                        } else {
                                            String[] split = scoreIndexRule.getScordPrimaryIds().split(";");
                                            Double scoreNew = scoreIndexRule.getScore();
                                            BigDecimal bigDecimal = new BigDecimal(scoreNew);
                                            for (String s : split) {
                                                ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                                        .eq(ScorePrimaryIndexSnapshot::getId, s)
                                                        .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                                                        .eq(ScorePrimaryIndexSnapshot::getButtonState,1));
                                                if (scorePrimaryIndex != null) {
                                                    bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                                                }
                                            }
                                            score = score.add(bigDecimal);
                                        }

                                        //根据命中code获取规则详细
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("code",scoreIndexRule.getIndexRule());
                                        hashMap.put("riskStatement", StrUtil.isNotBlank(scoreIndexRule.getDescription())?scoreIndexRule.getDescription().replaceAll("&gt;",">").replaceAll("&lt;","<"):null);
                                        ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = scorePrimaryIndexMapper.selectById(scoreIndexRule.getScorePrimaryId());
                                        hashMap.put("groupName",scorePrimaryIndexSnapshot!=null?scorePrimaryIndexSnapshot.getPrimaryIndex():"-");
                                        mapScore.add(hashMap);
                                        break;
                                    }
                                }
                                if(!flag){
                                    //获取默认指标
                                    ScoreIndexRuleSnapshot scoreIndexRuleSnapshotsDefault = scoreIndexRuleSnapshotMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                                            .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                                            .eq(ScoreIndexRuleSnapshot::getScordCardId, x.getModelId())
                                            .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                                            .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1)
                                            .eq(ScoreIndexRuleSnapshot::getDataState,0)
                                            .orderByAsc(ScoreIndexRuleSnapshot::getLevel));
                                    if (scoreIndexRuleSnapshotsDefault !=null) {
                                        //判断是否独立生效  否 获取权重并乘评分
                                        if (scoreIndexRuleSnapshotsDefault.getTakeEffect() == 1) {
                                            score = score.add(BigDecimal.valueOf(scoreIndexRuleSnapshotsDefault.getScore()));
                                        }else {
                                            String[] split = scoreIndexRuleSnapshotsDefault.getScordPrimaryIds().split(";");
                                            Double scoreNew = scoreIndexRuleSnapshotsDefault.getScore();
                                            BigDecimal bigDecimal = new BigDecimal(scoreNew);
                                            for (String s : split) {
                                                ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                                        .eq(ScorePrimaryIndexSnapshot::getId, s)
                                                        .eq(ScorePrimaryIndexSnapshot::getDataState,0)
                                                        .eq(ScorePrimaryIndexSnapshot::getButtonState,1));
                                                if (scorePrimaryIndex != null) {
                                                    bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                                                }
                                            }
                                            score = score.add(bigDecimal);
                                        }
                                        //根据命中code获取规则详细
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("code",scoreIndexRuleSnapshotsDefault.getIndexRule());
                                        hashMap.put("riskStatement",StrUtil.isNotBlank(scoreIndexRuleSnapshotsDefault.getDescription())?scoreIndexRuleSnapshotsDefault.getDescription().replaceAll("&gt;",">").replaceAll("&lt;","<"):null);
                                        ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = scorePrimaryIndexMapper.selectById(scoreIndexRuleSnapshotsDefault.getScorePrimaryId());
                                        hashMap.put("groupName",scorePrimaryIndexSnapshot!=null?scorePrimaryIndexSnapshot.getPrimaryIndex():"-");
                                        mapScore.add(hashMap);
                                    }
                                }
                            }
                            JSONObject resultJson =  new JSONObject();
                            resultJson.put("score",score.setScale(2, RoundingMode.HALF_UP));
                            resultJson.put("decisionList",mapScore);
                            modelRuleResultDTO.setScoreData(resultJson);
                        }
                    }
                    break;
                case 5:
                    JSONObject jsonObject = JSONObject.parseObject(x.getResponseValue());
                    JSONArray codeList = new JSONArray();
                    String stronglyRejectFlag = "0";
                    if(jsonObject !=null && jsonObject.size() !=0){
                        //命中code集合
                        JSONObject objectJSONObject = jsonObject.getJSONObject("data");
                        if(objectJSONObject !=null &&objectJSONObject.size() != 0 ){
                            codeList = objectJSONObject.getJSONArray("codeList");
                            //根据命中code获取规则详细
                            for (int i = 0; i < codeList.size(); i++) {
                                Map<String, Object> decisionLevelDeatil = rdeModelAntiFraudRuleRecordSnapshotMapper.getDecisionLevelDeatil(codeList.get(i).toString(), x.getModelId());
                                if (decisionLevelDeatil != null){
                                    mapRule.add(decisionLevelDeatil);
                                }
                            }
                        }
                    }
                    //引用的标准数据规则存储点
                    LambdaQueryWrapper<ModelRegularData> wrapperRegularData = Wrappers.lambdaQuery();
                    wrapperRegularData.eq(ModelRegularData::getTaskNumber,modelTaskRecordVO.getTaskNo())
                            .eq(ModelRegularData::getDataStatus,0)
                            .eq(ModelRegularData::getRuleCode, RuleConstants.RULE_MODEL);
                    List<ModelRegularData> modelRegularDataList = modelRegularDataMapper.selectList(wrapperRegularData);
                    modelRegularDataList.stream().forEach(data ->{
                        if (StringUtils.isNotEmpty(data.getRuleData())){
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                List<Map<String, Object>> list = objectMapper.readValue(data.getRuleData(), new TypeReference<List<Map<String, Object>>>() {
                                });
                                mapRule.addAll(list);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    // 根据code去重
                    Map<String, Map<String, Object>> uniqueMaps = new HashMap<>();
                    for (Map<String, Object> map : mapRule) {
                        String code = (String) map.get("code");
                        if (!uniqueMaps.containsKey(code)) {
                            uniqueMaps.put(code, map);
                        }
                    }
                    // 转换回List
                    List<Map<String, Object>> result = new ArrayList<>(uniqueMaps.values());
                    JSONObject ruleData = new JSONObject();
                    for (Map<String,Object> map: result) {
                        if (map.get("stronglyReject")!=null && map.get("stronglyReject").toString().equals("1")) {
                            stronglyRejectFlag = "1";
                        }
                    }
                    ruleData.put("access",stronglyRejectFlag);
                    ruleData.put("decisionList",result);
                    modelRuleResultDTO.setRuleData(ruleData);
                    break;
                case 2:
                    // 评级模型
                    JSONObject jsonObjectRate = JSONObject.parseObject(x.getResponseValue());
                    if(jsonObjectRate !=null && jsonObjectRate.size() !=0){
                        JSONObject dataRate = jsonObjectRate.getJSONObject("data");
                        if(dataRate != null) {
                            modelRuleResultDTO.setRateData(dataRate);
                        }
                    }
                    break;
                case 3:
                    // 额度模型
                    JSONObject jsonObjectQuota = JSONObject.parseObject(x.getResponseValue());
                    if(jsonObjectQuota !=null && jsonObjectQuota.size() !=0){
                        JSONObject dataQuota = jsonObjectQuota.getJSONObject("data");
                        if(dataQuota != null) {
                            modelRuleResultDTO.setQuotaData(dataQuota);
                           if (modelTaskRecordVO.getPolicyRequestList() != null && !modelTaskRecordVO.getPolicyRequestList().isEmpty()) {
                               try {
                                   // 从参数列表中提取所需参数
                                   String cname = null;
                                   String orderNo = null;
                                   for (PolicyRequestDTO param : modelTaskRecordVO.getPolicyRequestList()) {
                                       // 支持多种企业名称字段名变体：c_name, cname, cName, companyName, company_name
                                       if ("c_name".equals(param.getName()) || "cname".equals(param.getName())
                                               || "cName".equals(param.getName()) || "companyName".equals(param.getName())
                                               || "company_name".equals(param.getName())) {
                                           cname = param.getValue();
                                       } else if ("orderNo".equals(param.getName()) || "orderId".equals(param.getName())) {
                                           orderNo = param.getValue();
                                       }
                                   }
                                   log.info("cname:{},orderNo:{}", cname,orderNo);
                                   Map<String, Object> quotaResult = calculateCreditFacilityFromFinancialData(
                                           cname,
                                           modelTaskRecordVO.getUserIdentification(),
                                           orderNo
                                   );

                                   if (quotaResult != null && quotaResult.get("creditFacility") != null) {
                                       Object creditFacility = quotaResult.get("creditFacility");
                                       if (creditFacility != null) {
                                           BigDecimal creditAmount = new BigDecimal(creditFacility.toString());
                                           BigDecimal quotaRangePercent = new BigDecimal(dataQuota.getString("quotaRange"));
                                           // 计算额度: 授信额度 × (1 + 额度浮动范围/100)
                                           BigDecimal adjustmentFactor = BigDecimal.ONE.add(quotaRangePercent.divide(QuotaCalculationConstants.PERCENT_DIVISOR));
                                           BigDecimal finalQuota = creditAmount.multiply(adjustmentFactor);
                                           dataQuota.put("quota", finalQuota);
                                           modelRuleResultDTO.setQuotaData(dataQuota);
                                       }
                                   }
                               } catch (Exception e) {
                                   log.error("获取授信额度失败", e);
                               }
                           }
                        }
                    }
                    break;
                case 4:
                    // 定价模型
                    JSONObject jsonObjectPrice = JSONObject.parseObject(x.getResponseValue());
                    if(jsonObjectPrice !=null && jsonObjectPrice.size() !=0){
                        JSONObject dataPrice = jsonObjectPrice.getJSONObject("data");
                        if(dataPrice != null) {
                            modelRuleResultDTO.setPriceData(dataPrice);
                        }
                    }
                    break;
                case 6:
                    JSONObject jsonObjectSort = JSONObject.parseObject(x.getResponseValue());
                    if(jsonObjectSort !=null && jsonObjectSort.size() !=0){
                        JSONObject objectSort = jsonObjectSort.getJSONObject("data");
                        modelRuleResultDTO.setSortData(objectSort.getJSONObject("jsonObject"));
                    }
                    break;
            }
        });

        return modelRuleResultDTO;
    }

    /**
     * 执行评级模型
     * 基于radius表判断是否有配置,有配置才执行
     * @param scoreProcessNode 评分模型节点
     * @param modelTaskRecordVO 任务记录VO
     * @param scoreDecisionCode 评分模型决策结果
     */
    private void executeRateCardModel(ProcessNode scoreProcessNode, ModelTaskRecordVO modelTaskRecordVO, JSONObject scoreDecisionCode) {
        try {
            Integer scoreCardId = Integer.valueOf(scoreProcessNode.getRuleCode());

            // 1. 查找关联的评级模型
            LambdaQueryWrapper<RateCardRecordSnapshot> rateWrapper = Wrappers.lambdaQuery();
            rateWrapper.eq(RateCardRecordSnapshot::getScoreCardId, scoreCardId)
                    .eq(RateCardRecordSnapshot::getButtonState, 1)
                    .eq(RateCardRecordSnapshot::getDataState, 0);
            RateCardRecordSnapshot rateCardRecord = rateCardRecordSnapshotMapper.selectOne(rateWrapper);

            if (rateCardRecord == null) {
                log.info("评分模型ID={} 未关联评级模型,跳过评级计算", scoreCardId);
                return;
            }

            // 2. 检查是否有radius配置数据 (关键判断)
            LambdaQueryWrapper<RateCardRadiusSnapshot> radiusCheckWrapper = Wrappers.lambdaQuery();
            radiusCheckWrapper.eq(RateCardRadiusSnapshot::getRateCardId, rateCardRecord.getId())
                    .eq(RateCardRadiusSnapshot::getButtonState, 1);
            Long radiusCount = Long.valueOf(rateCardRadiusSnapshotMapper.selectCount(radiusCheckWrapper));

            if (radiusCount == null || radiusCount == 0) {
                log.info("评级模型ID={} 未配置radius数据,跳过评级计算", rateCardRecord.getId());
                return;
            }

            // 3. 从评分结果中提取score
            JSONObject scoreData = scoreDecisionCode.getJSONObject("data");
            if (scoreData == null) {
                log.warn("评分模型返回数据为空,跳过评级计算。决策引擎返回: code={}, msg={}, scoreCardId={}",
                    scoreDecisionCode.get("code"),
                    scoreDecisionCode.get("msg"),
                    scoreCardId);
                return;
            }

            // 计算评分 (与dataResponseForm中的逻辑一致)
            BigDecimal score = calculateScoreFromDecisionCode(scoreCardId, scoreData);
            if (score == null) {
                log.warn("无法计算评分,跳过评级计算");
                return;
            }

            // 4. 匹配评级区间
            List<RateCardRadiusSnapshot> rateRadiusList = rateCardRadiusSnapshotMapper.selectList(radiusCheckWrapper);
            RateCardRadiusSnapshot matchedRadius = null;
            for (RateCardRadiusSnapshot radius : rateRadiusList) {
                if (isScoreInRange(score, radius.getRateRange())) {
                    matchedRadius = radius;
                    break;
                }
            }

            if (matchedRadius == null) {
                log.warn("评分{} 未匹配到任何评级区间", score);
                return;
            }

            // 5. 构建评级模型执行结果并存储
            JSONObject rateResult = new JSONObject();
            JSONObject rateDataObj = new JSONObject();
            rateDataObj.put("score", score);
            rateDataObj.put("rate", matchedRadius.getStandardRate());
            rateDataObj.put("rateRange", matchedRadius.getRateRange());
            rateDataObj.put("rateRangeContent", matchedRadius.getRateRangeContent());
            rateDataObj.put("rateCard", rateCardRecord.getRateCard());
            rateDataObj.put("scoreCardId", scoreCardId);
            rateDataObj.put("rateCardId", rateCardRecord.getId());
            rateResult.put("code", 200);
            rateResult.put("msg", "操作成功");
            rateResult.put("data", rateDataObj);

            // 6. 存储评级模型执行记录
            ModelProcessData rateProcessData = new ModelProcessData();
            rateProcessData.setTaskNo(modelTaskRecordVO.getTaskNo());
            rateProcessData.setParameter(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
            rateProcessData.setResponseValue(rateResult.toJSONString());
            rateProcessData.setDeptId(modelTaskRecordVO.getDeptId());
            rateProcessData.setUserId(modelTaskRecordVO.getUserId());
            rateProcessData.setRuleCode(2); // moduleId=2代表评级模型
            rateProcessData.setModelId(String.valueOf(rateCardRecord.getId()));
            modelProcessDataMapper.insert(rateProcessData);

            log.info("评级模型执行成功: 评分={}, 评级={}", score, matchedRadius.getStandardRate());

            // 7. 调用额度卡公式需要的接口（补充评分卡未调用的接口）
            callQuotaFormulaInterfaces(rateCardRecord, modelTaskRecordVO);

            // 8. 执行额度模型
            executeQuotaCardModel(rateCardRecord, modelTaskRecordVO, matchedRadius.getStandardRate(), score);

        } catch (Exception e) {
            log.error("执行评级模型异常", e);
        }
    }

    /**
     * 执行额度模型
     * 基于radius表判断是否有配置,有配置才执行
     * @param rateCardRecord 评级模型记录
     * @param modelTaskRecordVO 任务记录VO
     * @param rate 评级结果
     * @param score 评分
     */
    private void executeQuotaCardModel(RateCardRecordSnapshot rateCardRecord, ModelTaskRecordVO modelTaskRecordVO, String rate, BigDecimal score) {
        try {
            // 1. 查找关联的额度模型
            LambdaQueryWrapper<QuotaCardRecordSnapshot> quotaWrapper = Wrappers.lambdaQuery();
            quotaWrapper.eq(QuotaCardRecordSnapshot::getRateCardId, rateCardRecord.getId())
                    .eq(QuotaCardRecordSnapshot::getButtonState, 1)
                    .eq(QuotaCardRecordSnapshot::getDataState, 0);
            QuotaCardRecordSnapshot quotaCardRecord = quotaCardRecordSnapshotMapper.selectOne(quotaWrapper);

            if (quotaCardRecord == null) {
                log.info("评级模型ID={} 未关联额度模型,跳过额度计算", rateCardRecord.getId());
                return;
            }

            // 2. 检查是否有radius配置数据 (关键判断)
            LambdaQueryWrapper<QuotaCardRadiusSnapshot> radiusCheckWrapper = Wrappers.lambdaQuery();
            radiusCheckWrapper.eq(QuotaCardRadiusSnapshot::getQuotaCardId, quotaCardRecord.getId())
                    .eq(QuotaCardRadiusSnapshot::getButtonState, 1);
            Long radiusCount = Long.valueOf(quotaCardRadiusSnapshotMapper.selectCount(radiusCheckWrapper));

            if (radiusCount == null || radiusCount == 0) {
                log.info("额度模型ID={} 未配置radius数据,跳过额度计算", quotaCardRecord.getId());
                return;
            }

            // 3. 根据评级匹配额度配置
            // 修复: 使用selectList避免多条记录导致TooManyResultsException
            // 优先选择根节点(parent_id为null或0)或按id排序取第一条
            // 注意: 使用standardName(额度评级)而不是standardRate(标准评级)进行匹配
            LambdaQueryWrapper<QuotaCardRadiusSnapshot> quotaRadiusWrapper = Wrappers.lambdaQuery();
            quotaRadiusWrapper.eq(QuotaCardRadiusSnapshot::getQuotaCardId, quotaCardRecord.getId())
                    .eq(QuotaCardRadiusSnapshot::getStandardName, rate)
                    .eq(QuotaCardRadiusSnapshot::getButtonState, 1)
                    .orderByAsc(QuotaCardRadiusSnapshot::getId) // 按ID升序,取最早创建的记录
                    .last("LIMIT 1"); // 只取第一条记录
            List<QuotaCardRadiusSnapshot> quotaRadiusList = quotaCardRadiusSnapshotMapper.selectList(quotaRadiusWrapper);
            QuotaCardRadiusSnapshot quotaRadius = quotaRadiusList.isEmpty() ? null : quotaRadiusList.get(0);

            if (quotaRadius == null) {
                log.warn("【额度计算】评级{} 未匹配到额度配置,quotaCardId={}", rate, quotaCardRecord.getId());
                return;
            }

            // 4. 计算具体额度金额
            BigDecimal calculatedQuota = null;
            String quotaCalculationDesc = "未计算";
            BigDecimal creditAmount = BigDecimal.ZERO;  // 标准额度(基准额度)

            try {
                // 4.1 从 data_calling 表提取字段值
                Map<String, Object> fieldValues = quotaCardRecordService.extractFieldValuesFromDataCalling(
                        quotaCardRecord,
                        modelTaskRecordVO.getTaskNo()
                );

                if (fieldValues.isEmpty()) {
                    creditAmount = BigDecimal.ZERO;
                } else {
                    // 4.2 使用公式计算标准额度
                    creditAmount = quotaCardRecordService.calculateStandardQuotaByFormula(
                            quotaCardRecord,
                            fieldValues
                    );
                }

                // 4.3 应用评级调整因子
                if (creditAmount != null && quotaRadius.getQuotaRange() != null) {
                    BigDecimal quotaRangePercent = new BigDecimal(quotaRadius.getQuotaRange());
                    // 计算额度: 标准额度 × (1 + 额度浮动范围/100)
                    // 标准额度BBB为100%基准,quotaRange为浮动比例(+50%/-25%/-75%/-100%等)
                    BigDecimal adjustmentFactor = BigDecimal.ONE.add(quotaRangePercent.divide(QuotaCalculationConstants.PERCENT_DIVISOR));
                    calculatedQuota = creditAmount.multiply(adjustmentFactor);
                    quotaCalculationDesc = String.format("标准额度%.2f × (1 + %s%%) = %.2f",
                            creditAmount, quotaRadius.getQuotaRange(), calculatedQuota);
                    log.info("【额度计算】最终额度计算成功: {}", quotaCalculationDesc);
                }

            } catch (Exception e) {
                log.error("【额度计算】计算额度失败", e);
                // 不暴露技术异常信息,统一设置为额度0
                calculatedQuota = BigDecimal.ZERO;
                quotaCalculationDesc = "计算失败: " + e.getMessage();
            }

            // 5. 构建额度模型执行结果并存储
            JSONObject quotaResult = new JSONObject();
            JSONObject quotaDataObj = new JSONObject();
            quotaDataObj.put("score", score);
            quotaDataObj.put("rate", rate);
            quotaDataObj.put("quota", calculatedQuota != null ? calculatedQuota : "未计算");
            quotaDataObj.put("quotaRange", quotaRadius.getQuotaRange());
            quotaDataObj.put("quotaRangeContent", quotaRadius.getQuotaRangeContent());
            quotaDataObj.put("quotaCalculationDesc", quotaCalculationDesc);
            quotaDataObj.put("quotaCard", quotaCardRecord.getQuotaCard());
            quotaDataObj.put("rateCardId", rateCardRecord.getId());
            quotaDataObj.put("quotaCardId", quotaCardRecord.getId());
            quotaResult.put("code", 200);
            quotaResult.put("msg", "操作成功");
            quotaResult.put("data", quotaDataObj);

            // 6. 存储额度模型执行记录
            ModelProcessData quotaProcessData = new ModelProcessData();
            quotaProcessData.setTaskNo(modelTaskRecordVO.getTaskNo());
            quotaProcessData.setParameter(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
            quotaProcessData.setResponseValue(quotaResult.toJSONString());
            quotaProcessData.setDeptId(modelTaskRecordVO.getDeptId());
            quotaProcessData.setUserId(modelTaskRecordVO.getUserId());
            quotaProcessData.setRuleCode(3); // moduleId=3代表额度模型
            quotaProcessData.setModelId(String.valueOf(quotaCardRecord.getId()));
            modelProcessDataMapper.insert(quotaProcessData);

            log.info("额度模型执行成功: 评分={}, 评级={}, 计算额度={}", score, rate,
                    calculatedQuota != null ? calculatedQuota : "未计算");

            // 8. 执行定价模型
            executePriceCardModel(quotaCardRecord, modelTaskRecordVO, rate, calculatedQuota);

        } catch (Exception e) {
            log.error("执行额度模型异常", e);
        }
    }

    /**
     * 调用额度卡公式需要的接口（补充评分卡未调用的接口）
     *
     * 实现逻辑：
     * 1. 查询额度卡配置的 formulaVariables
     * 2. 提取额度卡公式需要的 manageNo 列表
     * 3. 查询 data_calling 表，找出已经调用过的接口
     * 4. 调用缺失的接口，并保存到 data_calling 表
     *
     * @param rateCardRecord 评级卡记录
     * @param modelTaskRecordVO 任务记录VO
     */
    private void callQuotaFormulaInterfaces(RateCardRecordSnapshot rateCardRecord, ModelTaskRecordVO modelTaskRecordVO) {
        try {
            // 1. 查询额度卡配置
            LambdaQueryWrapper<QuotaCardRecordSnapshot> quotaWrapper = Wrappers.lambdaQuery();
            quotaWrapper.eq(QuotaCardRecordSnapshot::getRateCardId, rateCardRecord.getId())
                    .eq(QuotaCardRecordSnapshot::getButtonState, 1)
                    .eq(QuotaCardRecordSnapshot::getDataState, 0);
            QuotaCardRecordSnapshot quotaCard = quotaCardRecordSnapshotMapper.selectOne(quotaWrapper);

            if (quotaCard == null || StrUtil.isBlank(quotaCard.getFormulaVariables())) {
                log.info("额度卡不存在或未配置公式, rateCardId={}", rateCardRecord.getId());
                return;
            }

            // 2. 解析额度卡公式的 manageNo 列表
            Map<String, Map<String, Object>> variablesMap = JSON.parseObject(
                quotaCard.getFormulaVariables(),
                new com.alibaba.fastjson2.TypeReference<Map<String, Map<String, Object>>>() {}
            );

            if (variablesMap == null || variablesMap.isEmpty()) {
                log.info("额度卡公式变量为空, quotaCardId={}", quotaCard.getId());
                return;
            }

            Set<String> quotaManageNos = variablesMap.values().stream()
                .map(field -> (String) field.get("manageNo"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            if (quotaManageNos.isEmpty()) {
                log.info("额度卡公式未配置接口, quotaCardId={}", quotaCard.getId());
                return;
            }

            // 3. 查询 data_calling 表，看哪些接口已经被调用
            List<DataCalling> existingCalls = dataCallingMapper.selectList(
                new LambdaQueryWrapper<DataCalling>()
                    .eq(DataCalling::getOrderNo, modelTaskRecordVO.getTaskNo())
            );

            Set<String> calledManageNos = existingCalls.stream()
                .map(DataCalling::getManageNo)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            // 4. 找出额度卡需要但评分卡没调用的接口
            Set<String> missingManageNos = new HashSet<>(quotaManageNos);
            missingManageNos.removeAll(calledManageNos);

            if (missingManageNos.isEmpty()) {
                log.info("额度卡公式需要的接口已全部调用, quotaCardId={}, manageNos={}",
                        quotaCard.getId(), quotaManageNos);
                return;
            }

            log.info("额度卡公式需要额外调用接口, quotaCardId={}, missingManageNos={}",
                    quotaCard.getId(), missingManageNos);

            // 5. 调用缺失的接口
            for (String manageNo : missingManageNos) {
                try {
                    // 5.1 从 formulaVariables 中查找对应 manageNo 的接口配置
                    Map<String, Object> interfaceInfo = null;
                    for (Map<String, Object> fieldInfo : variablesMap.values()) {
                        if (manageNo.equals(fieldInfo.get("manageNo"))) {
                            interfaceInfo = fieldInfo;
                            break;
                        }
                    }

                    if (interfaceInfo == null) {
                        log.warn("在 formulaVariables 中未找到接口信息, manageNo={}", manageNo);
                        continue;
                    }

                    String sourceNo = (String) interfaceInfo.get("sourceNo");
                    String interfaceNo = (String) interfaceInfo.get("interfaceNo");

                    if (StrUtil.isBlank(sourceNo) || StrUtil.isBlank(interfaceNo)) {
                        log.warn("接口信息不完整, manageNo={}, sourceNo={}, interfaceNo={}. 请确保前端传入了 allPaths 字段",
                                manageNo, sourceNo, interfaceNo);
                        continue;
                    }

                    log.info("从 formulaVariables 获取接口信息, manageNo={}, sourceNo={}, interfaceNo={}",
                            manageNo, sourceNo, interfaceNo);

                    // 5.2 构建接口调用配置
                    Map<String, Object> interfaceConfig = new HashMap<>();
                    interfaceConfig.put("manageNo", manageNo);
                    interfaceConfig.put("sourceNo", sourceNo);
                    interfaceConfig.put("interfaceNo", interfaceNo);
                    interfaceConfig.put("objectFlag", "0");
                    interfaceConfig.put("objectName", "quotaFormulaData_" + manageNo);

                    // 5.3 调用接口（复用 CommonRuleFunctionDataNew 的逻辑）
                    log.info("开始调用额度卡额外接口, manageNo={}, sourceNo={}, interfaceNo={}",
                            manageNo, sourceNo, interfaceNo);

                    // getProcessEntry() 已经返回 Map<String, Object>，直接使用
                    Map<String, Object> processEntry = modelTaskRecordVO.getProcessEntry();
                    callAndSaveDataMiddleStationInterface(
                        interfaceConfig,
                        manageNo,
                        modelTaskRecordVO.getTaskNo(),
                        processEntry,
                        modelTaskRecordVO.getUserIdentity()
                    );

                    log.info("额度卡额外接口调用成功, manageNo={}", manageNo);

                } catch (Exception e) {
                    log.error("调用额度卡额外接口失败, manageNo={}", manageNo, e);
                }
            }

        } catch (Exception e) {
            log.error("调用额度卡公式接口失败, rateCardId={}", rateCardRecord.getId(), e);
            // 不抛异常，不影响主流程
        }
    }

    /**
     * 调用数据中台接口并保存结果到 data_calling 表
     * （简化版本，用于额度卡额外接口调用）
     */
    private void callAndSaveDataMiddleStationInterface(
            Map<String, Object> interfaceConfig,
            String manageNo,
            String taskNo,
            Map<String, Object> processEntry,
            Map<String, Object> userIdentity) throws Exception {

        // 1. 构建请求参数
        HashMap<String, Object> hashMap = new HashMap<>();
        processEntry.forEach((key, value) -> {
            if ("orderNo".equals(key)) {
                hashMap.put(key, taskNo);
            } else {
                hashMap.put(key, value);
            }
        });

        interfaceConfig.put("paramData", hashMap);

        // 2. 调用中台接口
        String appkey = null;
        String secret = null;
        com.risksmart.common.core.domain.R<com.value.decision.process.vo.InterfaceUser> interfaceUser =
            feignDataMiddleStationService.queryAppKeyByUserId(Integer.parseInt(userIdentity.get("userId").toString()));

        if (interfaceUser != null && interfaceUser.getCode() == 200) {
            InterfaceUser interfaceUserData = interfaceUser.getData();
            appkey = interfaceUserData.getAppKey();
            secret = interfaceUserData.getSecret();
        }

        EncryptDTO encryptBody = EncryptBodyUtil.createEncryptBody(
            appkey,
            secret,
            (String) interfaceConfig.get("manageNo"),
            (String) interfaceConfig.get("sourceNo"),
            (String) interfaceConfig.get("interfaceNo"),
            taskNo,
            hashMap
        );
        String result = cn.hutool.http.HttpUtil.post(interfaceRequestUrl, JSON.toJSONString(encryptBody));

        // 3. 解析结果并保存到 data_calling 表
        String status = "未查得";
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject != null && jsonObject.get("data") != null) {
            String code = jsonObject.getString("code");
            if ("200".equals(code) || "00".equals(code)) {
                status = "查得";
            }
        }

        // 4. 保存调用记录
        DataCalling dataCalling = new DataCalling();
        dataCalling.setManageNo(manageNo);
        dataCalling.setInterfaceType(null); // 简化处理，不设置类型
        dataCalling.setModelApplication(1); // 标记为模型应用
        dataCalling.setCallStatus(status);
        dataCalling.setOrderNo(taskNo);
        dataCalling.setCreateTime(java.time.LocalDateTime.now());
        dataCalling.setResponseBody(result);

        dataCallingMapper.insert(dataCalling);
    }

    /**
     * 执行定价模型
     * 基于评级和额度结果计算定价
     *
     * @param quotaCardRecord 额度卡记录
     * @param modelTaskRecordVO 任务记录VO
     * @param rate 评级结果
     * @param calculatedQuota 计算出的额度值
     */
    private void executePriceCardModel(
            QuotaCardRecordSnapshot quotaCardRecord,
            ModelTaskRecordVO modelTaskRecordVO,
            String rate,
            BigDecimal calculatedQuota) {

        try {
            // 1. 查找关联的定价模型(通过项目、业务代码和部门匹配,定价模型固定ruleCode=4)
            LambdaQueryWrapper<PriceCardRecordSnapshot> priceWrapper = Wrappers.lambdaQuery();
            priceWrapper.eq(PriceCardRecordSnapshot::getProjectCode, quotaCardRecord.getProjectCode())
                    .eq(PriceCardRecordSnapshot::getBusinessCode, quotaCardRecord.getBusinessCode())
                    .eq(PriceCardRecordSnapshot::getRuleCode, "4")  // 定价模型的ruleCode固定为4
                    .eq(PriceCardRecordSnapshot::getDeptId, quotaCardRecord.getDeptId())
                    .eq(PriceCardRecordSnapshot::getButtonState, 1)
                    .eq(PriceCardRecordSnapshot::getDataState, 0);
            PriceCardRecordSnapshot priceCardRecord = priceCardRecordSnapshotMapper.selectOne(priceWrapper);

            if (priceCardRecord == null) {
                log.info("未找到定价模型配置(projectCode={}, businessCode={}, ruleCode=4, deptId={}),跳过定价计算",
                        quotaCardRecord.getProjectCode(), quotaCardRecord.getBusinessCode(), quotaCardRecord.getDeptId());
                return;
            }

            // 2. 检查是否有radius配置数据
            LambdaQueryWrapper<PriceCardRadiusSnapshot> radiusCheckWrapper = Wrappers.lambdaQuery();
            radiusCheckWrapper.eq(PriceCardRadiusSnapshot::getPriceCardId, priceCardRecord.getId())
                    .eq(PriceCardRadiusSnapshot::getButtonState, 1);
            Long radiusCount = Long.valueOf(priceCardRadiusSnapshotMapper.selectCount(radiusCheckWrapper));

            if (radiusCount == null || radiusCount == 0) {
                log.info("定价模型ID={} 未配置radius数据,跳过定价计算", priceCardRecord.getId());
                return;
            }

            // 3. 将额度从元转换为万元
            BigDecimal quotaAmountInWan = calculatedQuota != null
                    ? calculatedQuota.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            // 4. 二维匹配：评级 + 额度区间
            List<PriceCardRadiusSnapshot> priceRadiusList = priceCardRadiusSnapshotMapper.selectList(radiusCheckWrapper);
            PriceCardRadiusSnapshot matchedRadius = null;

            for (PriceCardRadiusSnapshot radius : priceRadiusList) {
                // 第一维：匹配评级
                if (!radius.getStandardName().equals(rate)) {
                    continue;
                }

                // 第二维：匹配额度区间
                if (checkQuotaRange(quotaAmountInWan, radius)) {
                    matchedRadius = radius;
                    break;
                }
            }

            if (matchedRadius == null) {
                log.warn("评级={}, 额度={}万 未匹配到定价配置", rate, quotaAmountInWan);
                return;
            }

            // 5. 计算定价（转换为百分比）
            BigDecimal price = matchedRadius.getPriceRange() != null
                    ? new BigDecimal(matchedRadius.getPriceRange()).divide(BigDecimal.valueOf(100))
                    : null;

            // 6. 构建定价模型执行结果并存储
            JSONObject priceResult = new JSONObject();
            JSONObject priceDataObj = new JSONObject();
            priceDataObj.put("rate", rate);
            priceDataObj.put("quota", calculatedQuota);
            priceDataObj.put("quotaInWan", quotaAmountInWan);
            priceDataObj.put("price", price);
            priceDataObj.put("priceRange", matchedRadius.getPriceRange());
            priceDataObj.put("priceRangeContent", matchedRadius.getPriceRangeContent());
            priceDataObj.put("priceCard", priceCardRecord.getPriceCard());
            priceDataObj.put("quotaCardId", quotaCardRecord.getId());
            priceDataObj.put("priceCardId", priceCardRecord.getId());
            priceResult.put("code", 200);
            priceResult.put("msg", "操作成功");
            priceResult.put("data", priceDataObj);

            // 7. 存储定价模型执行记录
            ModelProcessData priceProcessData = new ModelProcessData();
            priceProcessData.setTaskNo(modelTaskRecordVO.getTaskNo());
            priceProcessData.setParameter(JSON.toJSONString(modelTaskRecordVO.getPolicyRequestList()));
            priceProcessData.setResponseValue(priceResult.toJSONString());
            priceProcessData.setDeptId(modelTaskRecordVO.getDeptId());
            priceProcessData.setUserId(modelTaskRecordVO.getUserId());
            priceProcessData.setRuleCode(4); // moduleId=4代表定价模型
            priceProcessData.setModelId(String.valueOf(priceCardRecord.getId()));
            modelProcessDataMapper.insert(priceProcessData);

            log.info("定价模型执行成功: 评级={}, 额度={}万, 定价={}%", rate, quotaAmountInWan, price);

        } catch (Exception e) {
            log.error("执行定价模型异常", e);
        }
    }

    /**
     * 检查额度是否在区间范围内
     *
     * @param quotaAmount 额度金额（万元）
     * @param radius 定价配置
     * @return 是否匹配
     */
    private boolean checkQuotaRange(BigDecimal quotaAmount, PriceCardRadiusSnapshot radius) {
        BigDecimal min = radius.getQuotaMin();
        BigDecimal max = radius.getQuotaMax();

        // 情况1：无限制（兼容旧数据）
        if (min == null && max == null) {
            return true;
        }

        // 情况2：只有下限（例如：>200万）
        if (min != null && max == null) {
            if (radius.getQuotaIncludeMin() != null && radius.getQuotaIncludeMin() == 1) {
                return quotaAmount.compareTo(min) >= 0;  // 额度 >= min
            } else {
                return quotaAmount.compareTo(min) > 0;   // 额度 > min
            }
        }

        // 情况3：只有上限（例如：≤100万）
        if (min == null && max != null) {
            if (radius.getQuotaIncludeMax() != null && radius.getQuotaIncludeMax() == 1) {
                return quotaAmount.compareTo(max) <= 0;  // 额度 <= max
            } else {
                return quotaAmount.compareTo(max) < 0;   // 额度 < max
            }
        }

        // 情况4：有上下限（例如：100 < 额度 ≤ 200）
        boolean aboveMin = (radius.getQuotaIncludeMin() != null && radius.getQuotaIncludeMin() == 1)
                ? quotaAmount.compareTo(min) >= 0
                : quotaAmount.compareTo(min) > 0;

        boolean belowMax = (radius.getQuotaIncludeMax() != null && radius.getQuotaIncludeMax() == 1)
                ? quotaAmount.compareTo(max) <= 0
                : quotaAmount.compareTo(max) < 0;

        return aboveMin && belowMax;
    }

    /**
     * 从决策结果中计算评分 (复用dataResponseForm中的逻辑)
     */
    private BigDecimal calculateScoreFromDecisionCode(Integer scoreCardId, JSONObject scoreData) {
        try {
            JSONArray codeListScore = scoreData.getJSONArray("codeList");

            // 评分卡计算模式与基础分
            ScoreCardRecordSnapshot scoreCardConfig = scoreCardRecordSnapshotMapper.selectOne(
                    new LambdaQueryWrapper<ScoreCardRecordSnapshot>()
                            .eq(ScoreCardRecordSnapshot::getId, scoreCardId)
                            .eq(ScoreCardRecordSnapshot::getDataState, 0)
            );
            boolean isDeductionMode = scoreCardConfig != null && "2".equals(scoreCardConfig.getArgument());
            BigDecimal baseScore = BigDecimal.ZERO;
            if (isDeductionMode && scoreCardConfig != null && StringUtils.isNotBlank(scoreCardConfig.getModelScore())) {
                try {
                    baseScore = new BigDecimal(scoreCardConfig.getModelScore());
                } catch (NumberFormatException e) {
                    log.warn("评分卡基础分格式错误，modelId={}，modelScore={}，使用默认值0", scoreCardId, scoreCardConfig.getModelScore());
                }
            }

            if (codeListScore == null) {
                codeListScore = new JSONArray();
            }

            BigDecimal ruleScoreSum = BigDecimal.ZERO;

            //获取所有规则的上级指标卡id
            List<Integer> primaryCardById = scoreIndexRuleSnapshotMapper.getPrimaryCardById(String.valueOf(scoreCardId));
            for (Integer primaryCardId : primaryCardById) {
                //根据指标卡获取规则 并判断规则是否命中
                List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleSnapshotMapper.selectList(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                        .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                        .eq(ScoreIndexRuleSnapshot::getScordCardId, scoreCardId)
                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                        .eq(ScoreIndexRuleSnapshot::getDefaultRule, 0)
                        .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                        .orderByAsc(ScoreIndexRuleSnapshot::getLevel));

                boolean flag = false;
                for (ScoreIndexRuleSnapshot scoreIndexRule : scoreIndexRuleSnapshots) {
                    if (codeListScore.contains(scoreIndexRule.getIndexRule())) {
                        flag = true;
                        if (scoreIndexRule.getTakeEffect() == 1) {
                            ruleScoreSum = ruleScoreSum.add(BigDecimal.valueOf(scoreIndexRule.getScore()));
                        } else {
                            String[] split = scoreIndexRule.getScordPrimaryIds().split(";");
                            BigDecimal bigDecimal = BigDecimal.valueOf(scoreIndexRule.getScore());
                            for (String s : split) {
                                ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                        .eq(ScorePrimaryIndexSnapshot::getId, s)
                                        .eq(ScorePrimaryIndexSnapshot::getDataState, 0)
                                        .eq(ScorePrimaryIndexSnapshot::getButtonState, 1));
                                if (scorePrimaryIndex != null) {
                                    bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                                }
                            }
                            ruleScoreSum = ruleScoreSum.add(bigDecimal);
                        }
                        break;
                    }
                }

                if (!flag) {
                    //获取默认指标
                    ScoreIndexRuleSnapshot scoreIndexRuleSnapshotsDefault = scoreIndexRuleSnapshotMapper.selectOne(new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                            .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                            .eq(ScoreIndexRuleSnapshot::getScordCardId, scoreCardId)
                            .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                            .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1)
                            .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                            .orderByAsc(ScoreIndexRuleSnapshot::getLevel));
                    if (scoreIndexRuleSnapshotsDefault != null) {
                        if (scoreIndexRuleSnapshotsDefault.getTakeEffect() == 1) {
                            ruleScoreSum = ruleScoreSum.add(BigDecimal.valueOf(scoreIndexRuleSnapshotsDefault.getScore()));
                        } else {
                            String[] split = scoreIndexRuleSnapshotsDefault.getScordPrimaryIds().split(";");
                            BigDecimal bigDecimal = BigDecimal.valueOf(scoreIndexRuleSnapshotsDefault.getScore());
                            for (String s : split) {
                                ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                        .eq(ScorePrimaryIndexSnapshot::getId, s)
                                        .eq(ScorePrimaryIndexSnapshot::getDataState, 0)
                                        .eq(ScorePrimaryIndexSnapshot::getButtonState, 1));
                                if (scorePrimaryIndex != null) {
                                    bigDecimal = bigDecimal.multiply(BigDecimal.valueOf(scorePrimaryIndex.getWeight()).divide(BigDecimal.valueOf(100)));
                                }
                            }
                            ruleScoreSum = ruleScoreSum.add(bigDecimal);
                        }
                    }
                }
            }

            // 扣减模式：基础分 - |扣减分|；加权求和模式：直接累加
            BigDecimal finalScore = isDeductionMode ? baseScore.subtract(ruleScoreSum.abs()) : ruleScoreSum;
            return finalScore.setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("计算评分失败", e);
            return null;
        }
    }

    /**
     * 判断评分是否在区间范围内
     * 支持格式: "0-60", "[0,60)", "(0,60]", "[0,60]", ">60", ">=60", "<60", "<=60"
     * @param score 评分
     * @param rangeStr 区间字符串
     * @return 是否在区间内
     */
    private boolean isScoreInRange(BigDecimal score, String rangeStr) {
        if (StrUtil.isBlank(rangeStr)) {
            return false;
        }

        rangeStr = rangeStr.trim();

        try {
            // 处理单边比较: >60, >=60, <60, <=60
            if (rangeStr.startsWith(">=")) {
                BigDecimal threshold = new BigDecimal(rangeStr.substring(2).trim());
                return score.compareTo(threshold) >= 0;
            } else if (rangeStr.startsWith(">")) {
                BigDecimal threshold = new BigDecimal(rangeStr.substring(1).trim());
                return score.compareTo(threshold) > 0;
            } else if (rangeStr.startsWith("<=")) {
                BigDecimal threshold = new BigDecimal(rangeStr.substring(2).trim());
                return score.compareTo(threshold) <= 0;
            } else if (rangeStr.startsWith("<")) {
                BigDecimal threshold = new BigDecimal(rangeStr.substring(1).trim());
                return score.compareTo(threshold) < 0;
            }

            // 处理区间: [0,60), (0,60], [0,60], (0,60), 0-60
            boolean leftInclude = rangeStr.startsWith("[");
            boolean rightInclude = rangeStr.endsWith("]");

            // 去除括号
            String content = rangeStr.replaceAll("[\\[\\]\\(\\)]", "");

            // 分割左右边界
            String[] parts = content.split("[-,]");
            if (parts.length != 2) {
                log.warn("区间格式不正确: {}", rangeStr);
                return false;
            }

            BigDecimal leftBound = new BigDecimal(parts[0].trim());
            BigDecimal rightBound = new BigDecimal(parts[1].trim());

            // 判断左边界
            boolean leftMatch = leftInclude ?
                    score.compareTo(leftBound) >= 0 :
                    score.compareTo(leftBound) > 0;

            // 判断右边界
            boolean rightMatch = rightInclude ?
                    score.compareTo(rightBound) <= 0 :
                    score.compareTo(rightBound) < 0;

            return leftMatch && rightMatch;

        } catch (Exception e) {
            log.error("解析区间字符串失败: {}", rangeStr, e);
            return false;
        }
    }


    /**
     * 查找关联的规则组和规则数据
     * 根据自建策略id  产品id 场景id 模型标识id  然后moudle_id为2,3的数据
     * moudle_id为2的通过版本控制表中根据groupId和版本号找到全部规则数据
     * moudle_id为3的通过版本控制表中根据规则id和版本号找到全部规则数据
     * @param ruleRecordReuseVO
     */
    public void getAssociationRuleData(RuleRecordReuseVO ruleRecordReuseVO){
        //根据自建策略id  产品id 场景id 模型标识id  然后moudle_id为2,3的数据
        LambdaQueryWrapper<RuleRecordReuse> twoWrapper = Wrappers.lambdaQuery();
        twoWrapper.eq(RuleRecordReuse::getRuleId,ruleRecordReuseVO.getRuleId())
                .eq(RuleRecordReuse::getDeptId,ruleRecordReuseVO.getDeptId())
                .eq(RuleRecordReuse::getMoudleId,2)
                .eq(RuleRecordReuse::getDataStatus,0);
        List<RuleRecordReuse> ruleRecordTwoList = ruleRecordReuseMapper.selectList(twoWrapper);
        //moudle_id为2的通过版本控制表中根据groupId和版本号找到全部规则数据
        ruleRecordTwoList.stream().forEach(x ->{

        });
    }


    private static final String API_URL = "http://192.168.1.187:7092/interfaceUserPermissions/queryAppkeyByUserId";

    public static String callRiskTestAPI(Integer userId, String authorization) throws IOException {
        // 构建完整的请求URL，包含查询参数
        String requestUrl = String.format("%s?userId=%d", API_URL, userId);
        URL url = new URL(requestUrl);

        // 打开HTTP连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 设置请求头
            connection.setRequestProperty("Authorization", authorization);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // 发送请求并获取响应码
            int responseCode = connection.getResponseCode();

            // 处理响应
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应内容
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                // 处理错误响应
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream())
                );
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                throw new IOException("HTTP error occurred: " + responseCode +
                        ", Error response: " + errorResponse.toString());
            }
        } finally {
            // 断开连接
            connection.disconnect();
        }
    }

    private String getModelNameSnapshot(List<ProcessNode> processNodeList) {
        if (CollectionUtils.isEmpty(processNodeList)) {
            return "-";
        }

        // 按module_id分组
        Map<Integer, List<String>> nodeGroup = processNodeList.stream()
                .filter(node -> StrUtil.isNotEmpty(node.getRuleCode()))
                .collect(Collectors.groupingBy(
                        ProcessNode::getModuleId,
                        Collectors.mapping(ProcessNode::getRuleCode, Collectors.toList())
                ));

        List<String> modelNames = new ArrayList<>();

        // 查询评分模型名称
        List<String> scoreRuleCodes = nodeGroup.getOrDefault(1, Collections.emptyList());
        if (CollectionUtils.isNotEmpty(scoreRuleCodes)) {
            List<ScoreCardRecordSnapshot> scoreCardList = scoreCardRecordSnapshotMapper.selectList(
                    Wrappers.lambdaQuery(ScoreCardRecordSnapshot.class)
                            .in(ScoreCardRecordSnapshot::getId, scoreRuleCodes)
                            .eq(ScoreCardRecordSnapshot::getDataState, 0)
            );
            modelNames.addAll(scoreCardList.stream().map(ScoreCardRecordSnapshot::getScoreCard).collect(Collectors.toList()));
        }

        // 查询规则/分类模型名称
        List<String> ruleOrSortRuleCodes = new ArrayList<>();
        ruleOrSortRuleCodes.addAll(nodeGroup.getOrDefault(5, Collections.emptyList()));
        ruleOrSortRuleCodes.addAll(nodeGroup.getOrDefault(6, Collections.emptyList()));

        if (CollectionUtils.isNotEmpty(ruleOrSortRuleCodes)) {
            List<RdeModelAntiFraudSnapshot> antiFraudList = rdeModelAntiFraudSnapshotMapper.selectList(
                    Wrappers.lambdaQuery(RdeModelAntiFraudSnapshot.class)
                            .in(RdeModelAntiFraudSnapshot::getId, ruleOrSortRuleCodes)
                            .eq(RdeModelAntiFraudSnapshot::getDataStatus, 0)
            );
            modelNames.addAll(antiFraudList.stream().map(RdeModelAntiFraudSnapshot::getName).collect(Collectors.toList()));
        }

        return String.join(",", modelNames);
    }

    /**
     * 报告响应形式
     * @param modelTaskRecordVO
     */
    @Override
    public EvaluationReportDTO getReportResponseForm(ModelTaskRecordVO modelTaskRecordVO) {
        EvaluationReportDTO reportDTO = new EvaluationReportDTO();

        // 1. 查询任务记录
        ModelTaskRecord taskRecord = modelTaskRecordMapper.selectOne(Wrappers.lambdaQuery(ModelTaskRecord.class)
                .eq(ModelTaskRecord::getTaskNo, modelTaskRecordVO.getTaskNo())
                .eq(ModelTaskRecord::getDeptId, modelTaskRecordVO.getDeptId()));
        if (taskRecord == null) {
            return null;
        }

        // 2. 填充报告基本信息
        ProcessPolicy processPolicy = processPolicyMapper.selectById(taskRecord.getProcessId());
        if (processPolicy != null) {
            reportDTO.setProcessName(processPolicy.getProcessStrategy());
            reportDTO.setProcessStrategy(processPolicy.getProcessStrategy());
        }
        reportDTO.setTaskNo(taskRecord.getTaskNo());
        Date reportStartTime = Date.from(taskRecord.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
        Date reportEndTime = new Date(); // 当前时间为结束时间
        reportDTO.setExecutionTime(reportStartTime);

        // 3. 填充新增的任务基本信息字段
        fillTaskBasicInfo(reportDTO, taskRecord, processPolicy);

        // 查询本次任务所有的数据中台调用记录，用于后续获取manageNo
        List<DataCalling> dataCallings = dataCallingMapper.selectList(Wrappers.lambdaQuery(DataCalling.class)
                .eq(DataCalling::getOrderNo, modelTaskRecordVO.getTaskNo()));
        log.info("任务[{}]查询到的data_calling记录: {}", modelTaskRecordVO.getTaskNo(), JSON.toJSONString(dataCallings));
        // 3. 查询流程节点定义
        List<ProcessNode> processNodes = processNodeMapper.selectList(Wrappers.lambdaQuery(ProcessNode.class)
                .eq(ProcessNode::getProcessStrategyId, taskRecord.getProcessId()));

        // 4. 查询所有模型的执行结果数据
        List<ModelProcessData> modelProcessDataList = modelProcessDataMapper.selectList(Wrappers.lambdaQuery(ModelProcessData.class)
                .eq(ModelProcessData::getTaskNo, modelTaskRecordVO.getTaskNo())
                .eq(ModelProcessData::getDataStatus, 0)
                .eq(ModelProcessData::getDeptId, modelTaskRecordVO.getDeptId()));

        // 5. 以流程定义为准，遍历处理每个节点，并只展示命中规则的模型
        List<EvaluationModuleDTO> modules = new ArrayList<>();
        Map<String, ModelProcessData> processDataMap = modelProcessDataList.stream()
                .collect(Collectors.toMap(ModelProcessData::getModelId, data -> data, (d1, d2) -> d1));

        // 按执行时间对节点进行排序，以确保持续时间计算的正确性
        processNodes.sort(Comparator.comparing(
                node -> {
                    ModelProcessData data = processDataMap.get(node.getRuleCode());
                    return data != null ? data.getCreateTime() : null;
                },
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        List<ProcessNode> nodesToProcess = processNodes;
        // 如果请求中指定了 moduleId，则只处理该模块
        if (modelTaskRecordVO.getModuleId() != null) {
            nodesToProcess = processNodes.stream()
                    .filter(node -> node.getRuleCode().equals(String.valueOf(modelTaskRecordVO.getModuleId())))
                    .collect(Collectors.toList());
        }

        for (ProcessNode node : nodesToProcess) {
            ModelProcessData processData = processDataMap.get(node.getRuleCode());
            // 只有执行过的模型才处理
            if (processData == null) {
                continue;
            }

            List<EvaluationRuleDetailDTO> details = new ArrayList<>();
            BigDecimal calculatedScore = null;

             // 解析引擎响应
            JSONObject responseJson = JSONObject.parseObject(processData.getResponseValue());
            if (responseJson == null || !responseJson.containsKey("data")) {
                continue;
            }
            JSONObject dataObject = responseJson.getJSONObject("data");
            if (dataObject == null || dataObject.isEmpty()) {
                continue;
            }

            // 根据模型类型填充 details
            switch (node.getModuleId()) {
                case 1: // 评分卡
                    calculatedScore = new BigDecimal(0);
                    JSONArray scoreCodeList = dataObject.getJSONArray("codeList");
                    // 修复：即使 codeList 为空，也需要遍历主指标并使用默认规则计算分数
                    // 这对于回溯场景尤为重要，因为回溯时可能没有接口数据，导致规则未触发
                    log.info("【报告生成-评分卡】开始计算, taskNo={}, modelId={}, codeList={}",
                        modelTaskRecordVO.getTaskNo(), processData.getModelId(), scoreCodeList);
                    List<Map<String, Object>> mapScore = new ArrayList<>();
                    List<Integer> primaryCardById = scoreIndexRuleSnapshotMapper.getPrimaryCardById(processData.getModelId());
                    log.info("【报告生成-评分卡】主指标数量: {}", primaryCardById != null ? primaryCardById.size() : 0);

                    // 批量查询所有主指标，构建缓存Map，避免N+1查询
                    Map<Integer, ScorePrimaryIndexSnapshot> primaryIndexMap = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(primaryCardById)) {
                        List<ScorePrimaryIndexSnapshot> primaryIndexList = scorePrimaryIndexMapper.selectList(
                            new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                                .in(ScorePrimaryIndexSnapshot::getId, primaryCardById)
                                .eq(ScorePrimaryIndexSnapshot::getDataState, 0)
                                .eq(ScorePrimaryIndexSnapshot::getButtonState, 1)
                        );
                        primaryIndexMap = primaryIndexList.stream()
                            .collect(Collectors.toMap(ScorePrimaryIndexSnapshot::getId, p -> p, (a, b) -> a));
                    }

                    // 构建用于权重计算的缓存（ID为String类型）
                    Map<String, ScorePrimaryIndexSnapshot> primaryIndexCacheForWeight = new HashMap<>();
                    primaryIndexMap.forEach((k, v) -> primaryIndexCacheForWeight.put(k.toString(), v));

                    for (Integer primaryCardId : primaryCardById) {
                        List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleSnapshotMapper.selectList(
                            new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                                .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                                .eq(ScoreIndexRuleSnapshot::getScordCardId, processData.getModelId())
                                .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                                .eq(ScoreIndexRuleSnapshot::getDefaultRule, 0)
                                .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                                .orderByAsc(ScoreIndexRuleSnapshot::getLevel)
                        );

                        boolean flag = false;
                        // 只有当 scoreCodeList 不为空时，才尝试匹配非默认规则
                        if (CollectionUtils.isNotEmpty(scoreCodeList)) {
                            for (ScoreIndexRuleSnapshot scoreIndexRule : scoreIndexRuleSnapshots) {
                                if (scoreCodeList.contains(scoreIndexRule.getIndexRule())) {
                                    flag = true;
                                    // 使用缓存版本的 calculateRuleScore 方法
                                    calculatedScore = calculatedScore.add(calculateRuleScore(scoreIndexRule, primaryIndexCacheForWeight));

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("code", scoreIndexRule.getIndexRule());
                                    hashMap.put("riskStatement", StrUtil.isNotBlank(scoreIndexRule.getDescription()) ? scoreIndexRule.getDescription().replaceAll(">", ">").replaceAll("<", "<") : null);
                                    // 从缓存中获取主指标
                                    ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = primaryIndexMap.get(scoreIndexRule.getScorePrimaryId());
                                    hashMap.put("groupName", scorePrimaryIndexSnapshot != null ? scorePrimaryIndexSnapshot.getPrimaryIndex() : "-");
                                    mapScore.add(hashMap);
                                    break;
                                }
                            }
                        }

                        // 未命中非默认规则时，使用默认规则
                        if (!flag) {
                            ScoreIndexRuleSnapshot scoreIndexRuleSnapshotsDefault = scoreIndexRuleSnapshotMapper.selectOne(
                                new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                                    .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                                    .eq(ScoreIndexRuleSnapshot::getScordCardId, processData.getModelId())
                                    .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                                    .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1)
                                    .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                                    .orderByAsc(ScoreIndexRuleSnapshot::getLevel)
                            );

                            if (scoreIndexRuleSnapshotsDefault != null) {
                                // 使用缓存版本的 calculateRuleScore 方法
                                calculatedScore = calculatedScore.add(calculateRuleScore(scoreIndexRuleSnapshotsDefault, primaryIndexCacheForWeight));

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("code", scoreIndexRuleSnapshotsDefault.getIndexRule());
                                hashMap.put("riskStatement", StrUtil.isNotBlank(scoreIndexRuleSnapshotsDefault.getDescription()) ? scoreIndexRuleSnapshotsDefault.getDescription().replaceAll(">", ">").replaceAll("<", "<") : null);
                                // 从缓存中获取主指标
                                ScorePrimaryIndexSnapshot scorePrimaryIndexSnapshot = primaryIndexMap.get(scoreIndexRuleSnapshotsDefault.getScorePrimaryId());
                                hashMap.put("groupName", scorePrimaryIndexSnapshot != null ? scorePrimaryIndexSnapshot.getPrimaryIndex() : "-");
                                mapScore.add(hashMap);
                            }
                        }
                    }
                    for (Map<String, Object> item : mapScore) {
                        EvaluationRuleDetailDTO detailDTO = new EvaluationRuleDetailDTO();
                        detailDTO.setRuleCode((String) item.get("code"));
                        detailDTO.setDimension((String) item.get("groupName"));
                        // 对于评分模型，字段含义重新定义
                        detailDTO.setRiskLevel(null); // 评分模型不需要风险等级
                        detailDTO.setHitAction((String) item.get("riskStatement"));
                        details.add(detailDTO);
                    }
                    if (calculatedScore != null) {
                        calculatedScore = calculatedScore.setScale(2, RoundingMode.HALF_UP);
                    }
                    log.info("【报告生成-评分卡】计算完成, taskNo={}, modelId={}, 评分卡分数={}, 详情数量={}",
                        modelTaskRecordVO.getTaskNo(), processData.getModelId(), calculatedScore, details.size());
                    break;

                case 2: // 评级卡模型
                    log.info("【报告生成】处理评级卡模型: taskNo={}, modelId={}", modelTaskRecordVO.getTaskNo(), processData.getModelId());
                    if (dataObject.containsKey("score") && dataObject.containsKey("rate")) {
                        calculatedScore = dataObject.getBigDecimal("score");
                        String rate = dataObject.getString("rate");
                        String rateRange = dataObject.getString("rateRange");
                        String rateRangeContent = dataObject.getString("rateRangeContent");

                        EvaluationRuleDetailDTO detailDTO = new EvaluationRuleDetailDTO();
                        detailDTO.setRuleCode("RATE_RESULT");
                        detailDTO.setDimension("评级结果");
                        detailDTO.setHitAction(String.format("评分: %.2f, 评级: %s, 评分范围: %s, 说明: %s",
                            calculatedScore, rate, rateRange, rateRangeContent));
                        details.add(detailDTO);

                        log.info("【报告生成】评级卡结果: score={}, rate={}", calculatedScore, rate);
                    }
                    break;

                case 3: // 额度卡模型
                    log.info("【报告生成】处理额度卡模型: taskNo={}, modelId={}", modelTaskRecordVO.getTaskNo(), processData.getModelId());
                    if (dataObject.containsKey("quota")) {
                        calculatedScore = dataObject.getBigDecimal("score");
                        String rate = dataObject.getString("rate");
                        Object quota = dataObject.get("quota");
                        String quotaCalculationDesc = dataObject.getString("quotaCalculationDesc");
                        String quotaRange = dataObject.getString("quotaRange");
                        String quotaRangeContent = dataObject.getString("quotaRangeContent");

                        // Detail 1: 评分结果
                        EvaluationRuleDetailDTO scoreDetail = new EvaluationRuleDetailDTO();
                        scoreDetail.setRuleCode("QUOTA_SCORE");
                        scoreDetail.setDimension("评分");
                        scoreDetail.setHitAction(String.format("评分: %.2f", calculatedScore));
                        details.add(scoreDetail);

                        // Detail 2: 评级结果
                        EvaluationRuleDetailDTO rateDetail = new EvaluationRuleDetailDTO();
                        rateDetail.setRuleCode("QUOTA_RATE");
                        rateDetail.setDimension("评级");
                        rateDetail.setHitAction(String.format("评级: %s, 额度范围: %s, 说明: %s",
                            rate, quotaRange != null ? quotaRange : "--N/A--",
                            quotaRangeContent != null ? quotaRangeContent : "--N/A--"));
                        details.add(rateDetail);

                        // Detail 3: 额度结果
                        EvaluationRuleDetailDTO quotaDetail = new EvaluationRuleDetailDTO();
                        quotaDetail.setRuleCode("QUOTA_AMOUNT");
                        quotaDetail.setDimension("授信额度");
                        quotaDetail.setHitAction(String.format("授信额度: %s, 计算说明: %s",
                            quota, quotaCalculationDesc != null ? quotaCalculationDesc : "--N/A--"));
                        details.add(quotaDetail);

                        log.info("【报告生成】额度卡结果: score={}, rate={}, quota={}", calculatedScore, rate, quota);
                    }
                    break;

                case 5: // 规则模型
                    JSONArray ruleCodeList = dataObject.getJSONArray("codeList");
                    if (CollectionUtils.isNotEmpty(ruleCodeList)) {
                        for (Object code : ruleCodeList) {
                            RdeModelDecisionCodeLevelSnapshot codeLevel = rdeModelDecisionCodeLevelSnapshotMapper.selectOne(
                                    new LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot>()
                                            .eq(RdeModelDecisionCodeLevelSnapshot::getCode, code.toString())
                                            .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0).last("LIMIT 1"));
                            if (codeLevel != null) {
                                EvaluationRuleDetailDTO detailDTO = new EvaluationRuleDetailDTO();
                                detailDTO.setId(Long.valueOf(codeLevel.getId()));
                                detailDTO.setRiskLevel(RiskLevelEnum.getDescriptionByCode(Integer.valueOf(codeLevel.getLevel())));
                                detailDTO.setRuleCode(codeLevel.getCode());
                                detailDTO.setHitAction(codeLevel.getContent());
                                details.add(detailDTO);
                            }
                        }
                    }
                    break;
                case 6: // 分类模型
                    JSONObject jsonObject = dataObject.getJSONObject("jsonObject");
                    if (jsonObject == null) {
                        break;
                    }

                    // 优先从 codeList 读取完整的规则列表（新格式，避免 labelAll 被覆盖的问题）
                    JSONArray classifyCodeArray = dataObject.getJSONArray("codeList");
                    List<String> classifyCodeList = new ArrayList<>();

                    if (CollectionUtils.isNotEmpty(classifyCodeArray)) {
                        // 从 codeList 读取（新数据格式）
                        classifyCodeList.addAll(classifyCodeArray.toJavaList(String.class));
                        log.info("【报告生成】从 codeList 读取分类规则: {}", classifyCodeList);
                    } else {
                        // 兼容旧数据：从 labelAll 读取
                        log.info("【报告生成】codeList 为空，从 labelAll 读取分类规则（兼容旧数据）");
                        for (String key : jsonObject.keySet()) {
                            if ("json".equals(key) || "companyModules".equals(key)) {
                                continue;
                            }
                            Object value = jsonObject.get(key);
                            if (value instanceof JSONObject) {
                                JSONObject interfaceData = (JSONObject) value;
                                if (interfaceData.containsKey("labelAll")) {
                                    String labelAll = interfaceData.getString("labelAll");
                                    if (StringUtils.isNotEmpty(labelAll)) {
                                        classifyCodeList.addAll(Arrays.asList(labelAll.split(",")));
                                    }
                                }
                            }
                        }
                        // 也检查 companyModules 中的数组
                        if (jsonObject.containsKey("companyModules")) {
                            JSONObject companyModules = jsonObject.getJSONObject("companyModules");
                            if (companyModules != null) {
                                for (String key : companyModules.keySet()) {
                                    Object arrValue = companyModules.get(key);
                                    if (arrValue instanceof JSONArray) {
                                        JSONArray arr = (JSONArray) arrValue;
                                        for (int i = 0; i < arr.size(); i++) {
                                            JSONObject element = arr.getJSONObject(i);
                                            if (element != null && element.containsKey("labelAll")) {
                                                String labelAll = element.getString("labelAll");
                                                if (StringUtils.isNotEmpty(labelAll)) {
                                                    classifyCodeList.addAll(Arrays.asList(labelAll.split(",")));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    List<EvaluationRuleDetailDTO> hitRulesList = new ArrayList<>();
                    List<EvaluationRuleDetailDTO> dataTablesList = new ArrayList<>();

                    // 构建接口类型映射（一次性构建，避免重复）
                    Map<String, String> interfaceTypeMap = dataCallings.stream()
                            .filter(dc -> StringUtils.isNotEmpty(dc.getManageNo()) && StringUtils.isNotEmpty(dc.getInterfaceType()))
                            .collect(Collectors.toMap(DataCalling::getManageNo, DataCalling::getInterfaceType, (v1, v2) -> v1));

                    // 1. 处理对象形式数据（动态遍历所有对象类型接口）
                    for (String key : jsonObject.keySet()) {
                        // 跳过固定容器字段（json 和 companyModules 是固定的数据容器，不是接口数据）
                        if ("json".equals(key) || "companyModules".equals(key)) {
                            continue;
                        }

                        // 尝试获取对象类型的接口数据
                        Object value = jsonObject.get(key);
                        if (!(value instanceof JSONObject)) {
                            continue; // 跳过非对象类型的数据
                        }

                        JSONObject interfaceData = (JSONObject) value;
                        if (interfaceData == null || interfaceData.isEmpty()) {
                            continue;
                        }

                        // 构建数据表（不再从 labelAll 提取规则，已在前面从 codeList 提取）
                        String manageNo = interfaceData.getString("manage_no");
                        String interfaceName = interfaceTypeMap.getOrDefault(manageNo, key);
                        EvaluationRuleDetailDTO dataDetailDTO = new EvaluationRuleDetailDTO();
                        dataDetailDTO.setRuleCode(interfaceName);
                        dataDetailDTO.setHitAction("接口 [" + interfaceName + "] 返回 1 条数据");

                        List<Map<String, Object>> typedDynamicData = new ArrayList<>();
                        typedDynamicData.add(interfaceData);
                        dataDetailDTO.setDynamicData(typedDynamicData);
                        dataDetailDTO.setDynamicHeaders(getHeadersForTask(dataCallings));

                        dataTablesList.add(dataDetailDTO);
                    }

                    // 2. 处理数组形式数据（构建数据表）
                    if (jsonObject.containsKey("companyModules")) {
                        JSONObject companyModules = jsonObject.getJSONObject("companyModules");
                        for (String key : companyModules.keySet()) {
                            JSONArray arr = companyModules.getJSONArray(key);
                            if (CollectionUtils.isNotEmpty(arr)) {
                                // 构建数据表（不再从 labelAll 提取规则，已在前面从 codeList 提取）
                                JSONObject firstElement = arr.getJSONObject(0);
                                String manageNo = firstElement != null ? firstElement.getString("manage_no") : null;
                                String interfaceName = interfaceTypeMap.getOrDefault(manageNo, key);
                                processDynamicData(dataTablesList, interfaceName, arr, dataCallings);
                            }
                        }
                    }

                    // 3. 处理提取到的所有命中规则
                    if (CollectionUtils.isNotEmpty(classifyCodeList)) {
                        List<String> distinctCodeList = classifyCodeList.stream().distinct().collect(Collectors.toList());
                        for (String code : distinctCodeList) {
                            RdeModelDecisionCodeLevelSnapshot codeLevel = rdeModelDecisionCodeLevelSnapshotMapper.selectOne(
                                    new LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot>()
                                            .eq(RdeModelDecisionCodeLevelSnapshot::getCode, code.trim())
                                            .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0).last("LIMIT 1"));
                            if (codeLevel != null) {
                                EvaluationRuleDetailDTO ruleDetailDTO = new EvaluationRuleDetailDTO();
                                ruleDetailDTO.setId(Long.valueOf(codeLevel.getId()));
                                ruleDetailDTO.setRiskLevel(RiskLevelEnum.getDescriptionByCode(Integer.valueOf(codeLevel.getLevel())));
                                ruleDetailDTO.setRuleCode(codeLevel.getCode());
                                ruleDetailDTO.setHitAction(codeLevel.getContent());
                                hitRulesList.add(ruleDetailDTO);
                            }
                        }
                    }

                    // 4. 将分离后的数据封装到 details 列表中，以实现层级关系
                    if (CollectionUtils.isNotEmpty(hitRulesList)) {
                        for (EvaluationRuleDetailDTO hitRule : hitRulesList) {
                            // 查找与当前命中规则相关的 dataTable
                            dataTablesList.stream()
                                .filter(dataTable -> {
                                    // 检查 dataTable 的 dynamicData 中是否有任何一项的 labelAll 包含当前规则的 ruleCode
                                    List<Map<String, Object>> dynamicData = dataTable.getDynamicData();
                                    if (CollectionUtils.isNotEmpty(dynamicData)) {
                                        return dynamicData.stream().anyMatch(item -> {
                                            String labelAll = (String) item.get("labelAll");
                                            return labelAll != null && Arrays.asList(labelAll.split(",")).contains(hitRule.getRuleCode());
                                        });
                                    }
                                    return false;
                                })
                                .findFirst()
                                .ifPresent(matchedDataTable -> {
                                    // 将找到的 dataTable 的 dynamicData 和 dynamicHeaders 附加到 hitRule 中
                                    hitRule.setDynamicData(matchedDataTable.getDynamicData());
                                    hitRule.setDynamicHeaders(matchedDataTable.getDynamicHeaders());
                                });
                        }
                    }
                    // 4. 将合并后的 hitRulesList 添加到 details 中
                    details.addAll(hitRulesList);
                    break;
            }

            if (CollectionUtils.isNotEmpty(details)) {
                EvaluationModuleDTO moduleDTO = new EvaluationModuleDTO();
                if (node.getModuleId() == 1) { // 评分模型
                    ScoreCardRecordSnapshot scoreCard = scoreCardRecordSnapshotMapper.selectById(processData.getModelId());
                    moduleDTO.setModuleTitle(scoreCard != null ? scoreCard.getScoreCard() : node.getRuleName());
                } else if (StringUtils.isNumeric(processData.getModelId())) { // 规则和分类模型
                    RdeModelAntiFraud rdeModelAntiFraud = rdeModelAntiFraudMapper.selectById(Integer.parseInt(processData.getModelId()));
                    moduleDTO.setModuleTitle(rdeModelAntiFraud != null ? rdeModelAntiFraud.getName() : node.getRuleName());
                } else {
                    moduleDTO.setModuleTitle(node.getRuleName());
                }
                // 根据模型执行结果设置状态和关键结果
                String modelStatus = "通过";
                String keyResult = "无风险";

                switch (node.getModuleId()) {
                    case 1: // 评分模型
                        if (calculatedScore != null) {
                            int score = calculatedScore.intValue();
                            if (score < 400) {
                                modelStatus = "预警";
                                keyResult = "信用评分较低(" + score + "分)";
                            } else if (score < 600) {
                                modelStatus = "通过";
                                keyResult = "信用评分中等(" + score + "分)";
                            } else {
                                modelStatus = "通过";
                                keyResult = "信用评分良好(" + score + "分)";
                            }
                        }
                        break;

                    case 2: // 评级卡模型
                        if (calculatedScore != null && dataObject.containsKey("rate")) {
                            String rate = dataObject.getString("rate");
                            modelStatus = "通过";
                            keyResult = String.format("评分: %.2f, 评级: %s", calculatedScore, rate);
                            // 设置rate字段
                            moduleDTO.setRate(rate);
                        }
                        break;

                    case 3: // 额度卡模型
                        if (dataObject.containsKey("quota")) {
                            String rate = dataObject.getString("rate");
                            Object quota = dataObject.get("quota");
                            String quotaCalculationDesc = dataObject.getString("quotaCalculationDesc");
                            modelStatus = "通过";
                            keyResult = String.format("评级: %s, 授信额度: %s", rate, quota);
                            // 设置rate、quota、quotaCalculationDesc字段
                            moduleDTO.setRate(rate);
                            moduleDTO.setQuota(quota);
                            moduleDTO.setQuotaCalculationDesc(quotaCalculationDesc);
                        }
                        break;

                    case 5: // 规则模型
                        if (CollectionUtils.isNotEmpty(details)) {
                            boolean hasLevel1 = details.stream()
                                .anyMatch(detail -> "1".equals(detail.getRiskLevel()));
                            boolean hasLevel3 = details.stream()
                                .anyMatch(detail -> "3".equals(detail.getRiskLevel()));
                            boolean hasLevel5 = details.stream()
                                .anyMatch(detail -> "5".equals(detail.getRiskLevel()));

                            if (hasLevel5) {
                                modelStatus = "拒绝";
                                keyResult = "命中拒绝规则";
                            } else if (hasLevel3) {
                                modelStatus = "预警";
                                keyResult = "命中预警规则";
                            } else if (hasLevel1) {
                                modelStatus = "通过";
                                keyResult = "命中通过规则";
                            } else {
                                modelStatus = "通过";
                                keyResult = "未命中任何规则";
                            }
                        } else {
                            modelStatus = "通过";
                            keyResult = "未命中任何规则";
                        }
                        break;

                    case 6: // 分类模型
                        // 调整统计逻辑，以适应新的details结构
                        if (CollectionUtils.isNotEmpty(details)) {
                            // 统计逻辑保持不变，keyResult现在能反映总体情况
                            int totalDataCount = details.stream()
                                    .filter(d -> d.getDynamicData() != null)
                                    .mapToInt(d -> d.getDynamicData().size())
                                    .sum();

                            if (totalDataCount > 0) {
                                if (totalDataCount >= 10) {
                                    modelStatus = "预警";
                                    keyResult = "数据量较大(" + totalDataCount + "条)";
                                } else if (totalDataCount >= 5) {
                                    modelStatus = "通过";
                                    keyResult = "存在数据(" + totalDataCount + "条)";
                                } else {
                                    modelStatus = "通过";
                                    keyResult = "数据量较少(" + totalDataCount + "条)";
                                }
                            } else {
                                modelStatus = "通过";
                                keyResult = "无分类数据";
                            }
                        } else {
                            modelStatus = "通过";
                            keyResult = "未命中任何规则";
                        }
                        break;
                }

                moduleDTO.setStatus(modelStatus);
                moduleDTO.setKeyResult(keyResult);
                moduleDTO.setModelType(mapModelType(node.getModuleId()));
                moduleDTO.setModelId(processData.getModelId());
                Date startTime = Date.from(processData.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
                moduleDTO.setStartTime(startTime);

                // 计算模型间的实际间隔时间
                Date taskStartTime = Date.from(taskRecord.getCreateTime().atZone(ZoneId.systemDefault()).toInstant());
                Date modelUpdateTime = Date.from(processData.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant());

                // 确定基准时间：第一个模型用任务开始时间，后续模型用上一个模型的结束时间
                Date baseTime = modules.isEmpty() ? taskStartTime : modules.get(modules.size() - 1).getEndTime();
                long duration = modelUpdateTime.getTime() - baseTime.getTime();

                // 如果计算出的时间不合理（负数或0），使用默认值1秒
                Date modelEndTime;
                if (duration <= 0) {
                    duration = 1000L; // 默认1秒
                    modelEndTime = new Date(baseTime.getTime() + duration);
                } else {
                    modelEndTime = modelUpdateTime;
                }

                moduleDTO.setEndTime(modelEndTime);
                moduleDTO.setDuration(duration);

                if (node.getModuleId() == 1) {
                    moduleDTO.setFinalScore(calculatedScore);
                }
                moduleDTO.setDetails(details);
                modules.add(moduleDTO);
            }
        }

        reportDTO.setModules(modules);

        // 4. 填充决策结果信息
        fillDecisionInfo(reportDTO, modules, reportStartTime, reportEndTime);

        // 5. 生成分析建议
        try {
            ModelRuleResultDTO modelResults = new ModelRuleResultDTO();
            // 构建模型结果数据
            for (EvaluationModuleDTO module : modules) {
                // 评分模型没有moduleId，用modelType来判断
                if ("评分模型".equals(module.getModelType())) {
                    JSONObject scoreData = new JSONObject();
                    scoreData.put("finalScore", module.getFinalScore());
                    modelResults.setScoreData(scoreData);
                    } else if ("规则模型".equals(module.getModelType())) {
                        JSONObject ruleData = new JSONObject();
                        ruleData.put("hitRules", module.getDetails());
                        modelResults.setRuleData(ruleData);
                    } else if ("分类模型".equals(module.getModelType())) {
                        // 适配新的分层details结构
                        module.getDetails().stream()
                                .filter(container -> "dataTables".equals(container.getRuleCode()) && CollectionUtils.isNotEmpty(container.getDynamicData()))
                                .findFirst()
                                .ifPresent(container -> {
                                    List<Map<String, Object>> dataTables = container.getDynamicData();
                                    for (Map<String, Object> tableMap : dataTables) {
                                        // 从Map中恢复出 dataTable DTO 的关键信息
                                        String interfaceName = (String) tableMap.get("ruleCode");
                                        List<Map<String, Object>> dynamicData = (List<Map<String, Object>>) tableMap.get("dynamicData");

                                        if (StringUtils.isNotEmpty(interfaceName) && CollectionUtils.isNotEmpty(dynamicData)) {
                                            JSONObject sortData = modelResults.getSortData();
                                            if (sortData == null) {
                                                sortData = new JSONObject();
                                                sortData.put("companyModules", new JSONObject());
                                                modelResults.setSortData(sortData);
                                            }
                                            sortData.getJSONObject("companyModules").put(interfaceName, dynamicData);
                                        }
                                    }
                                });
                    }
            }

            // 计算总评分
            try {
                EvaluationReportDTO.TotalScoreInfo totalScoreInfo = totalScoreCalculationService.calculateTotalScore(modules, modelResults);
                reportDTO.setTotalScoreInfo(totalScoreInfo);

                // 更新决策信息中的风险评分和人工复核判断
                if (reportDTO.getDecisionInfo() != null) {
                    reportDTO.getDecisionInfo().setRiskScore(totalScoreInfo.getFinalScore());

                    // 基于总评分判断是否需要人工复核
                    boolean needManualReview = totalScoreCalculationService.shouldRequireManualReview(
                        totalScoreInfo.getFinalScore(), totalScoreInfo.getScoreLevel());
                    reportDTO.getDecisionInfo().setNeedManualReview(needManualReview ? "是" : "否");

                    // 更新决策等级
                    reportDTO.getDecisionInfo().setDecisionLevel(totalScoreInfo.getScoreLevel());

                    // 获取人工复核建议
                    String reviewSuggestion = totalScoreCalculationService.getManualReviewSuggestion(
                        totalScoreInfo.getFinalScore(), totalScoreInfo.getScoreLevel());
                    // 可以将建议添加到决策描述中
                    if (StringUtils.isEmpty(reportDTO.getDecisionInfo().getDecisionDescription())) {
                        reportDTO.getDecisionInfo().setDecisionDescription(reviewSuggestion);
                    }
                }
                // 如果数据库中没有分数，现在保存（兼容数据库旧数据）
                if (taskRecord.getTotalScore() == null && totalScoreInfo.getFinalScore() != null) {
                    log.info("检测到历史任务未计算分数，现在补充计算: taskNo={}", modelTaskRecordVO.getTaskNo());
                    ModelTaskRecord recordToUpdate = new ModelTaskRecord();
                    recordToUpdate.setTotalScore(new BigDecimal(totalScoreInfo.getFinalScore()));
                    modelTaskRecordMapper.update(recordToUpdate,
                        Wrappers.lambdaUpdate(ModelTaskRecord.class)
                            .eq(ModelTaskRecord::getTaskNo, modelTaskRecordVO.getTaskNo())
                    );
                }
            } catch (Exception e) {
                log.error("计算总评分失败", e);
                // 即使计算失败也不影响主流程
            }
            // 生成分析建议
            Map<String, Object> decisionInfoMap = new HashMap<>();
            if (reportDTO.getDecisionInfo() != null) {
                decisionInfoMap.put("needManualReview", reportDTO.getDecisionInfo().getNeedManualReview());
                decisionInfoMap.put("riskScore", reportDTO.getDecisionInfo().getRiskScore());
            }
            EvaluationReportDTO.AnalysisRecommendation recommendation =
                analysisRecommendationService.generateRecommendation(
                    taskRecord.getTaskNo(), modules, modelResults, decisionInfoMap);
            reportDTO.setAnalysisRecommendation(recommendation);
        } catch (Exception e) {
            log.error("生成分析建议失败", e);
            // 即使生成失败也不影响主流程
        }


        return reportDTO;
    }

    /**
     * 计算并持久化任务总评分
     * 从 getReportResponseForm 中提取的核心计算逻辑
     * 用于在任务执行完成后自动计算分数，确保 model_task_record.total_score 字段有值
     *
     * @param taskNo 任务编号
     * @param deptId 部门ID
     * @return 计算后的总评分，如果计算失败返回null
     */
    public BigDecimal calculateAndSaveTotalScore(String taskNo, Integer deptId) {
        try {
            log.info("开始计算任务总评分: taskNo={}, deptId={}", taskNo, deptId);

            // 1. 查询任务记录
            ModelTaskRecord taskRecord = modelTaskRecordMapper.selectOne(
                Wrappers.lambdaQuery(ModelTaskRecord.class)
                    .eq(ModelTaskRecord::getTaskNo, taskNo)
                    .eq(ModelTaskRecord::getDeptId, deptId)
            );

            if (taskRecord == null) {
                log.warn("任务不存在，无法计算分数: taskNo={}", taskNo);
                return null;
            }

            // 2. 查询流程节点定义
            List<ProcessNode> processNodes = processNodeMapper.selectList(
                Wrappers.lambdaQuery(ProcessNode.class)
                    .eq(ProcessNode::getProcessStrategyId, taskRecord.getProcessId())
            );

            if (CollectionUtils.isEmpty(processNodes)) {
                log.warn("流程节点为空，无法计算分数: taskNo={}", taskNo);
                return null;
            }

            // 3. 查询模型执行结果
            List<ModelProcessData> modelProcessDataList = modelProcessDataMapper.selectList(
                Wrappers.lambdaQuery(ModelProcessData.class)
                    .eq(ModelProcessData::getTaskNo, taskNo)
                    .eq(ModelProcessData::getDataStatus, 0)
                    .eq(ModelProcessData::getDeptId, deptId)
            );

            if (CollectionUtils.isEmpty(modelProcessDataList)) {
                log.warn("模型执行结果为空，无法计算分数: taskNo={}", taskNo);
                return null;
            }

            // 4. 查询数据中台调用记录（分类模型需要）
            List<DataCalling> dataCallings = dataCallingMapper.selectList(
                Wrappers.lambdaQuery(DataCalling.class)
                    .eq(DataCalling::getOrderNo, taskNo)
            );

            // 5. 构建模型模块列表（复用 getReportResponseForm 的逻辑）
            List<EvaluationModuleDTO> modules = buildEvaluationModules(
                processNodes, modelProcessDataList, dataCallings, taskRecord
            );

            if (CollectionUtils.isEmpty(modules)) {
                log.warn("未生成任何评估模块，无法计算分数: taskNo={}", taskNo);
                return null;
            }

            // 6. 构建模型结果数据
            ModelRuleResultDTO modelResults = buildModelRuleResult(modules);

            // 7. 计算总评分
            EvaluationReportDTO.TotalScoreInfo totalScoreInfo =
                totalScoreCalculationService.calculateTotalScore(modules, modelResults);

            if (totalScoreInfo != null && totalScoreInfo.getFinalScore() != null) {
                // 8. 持久化分数
                BigDecimal finalScore = new BigDecimal(totalScoreInfo.getFinalScore());
                ModelTaskRecord recordToUpdate = new ModelTaskRecord();
                recordToUpdate.setTotalScore(finalScore);

                modelTaskRecordMapper.update(recordToUpdate,
                    Wrappers.lambdaUpdate(ModelTaskRecord.class)
                        .eq(ModelTaskRecord::getTaskNo, taskNo)
                );

                log.info("任务总评分计算完成: taskNo={}, score={}, level={}",
                    taskNo, finalScore, totalScoreInfo.getScoreLevel());
                return finalScore;
            }

            log.warn("总评分信息为空: taskNo={}", taskNo);
            return null;
        } catch (Exception e) {
            log.error("计算任务总评分失败: taskNo={}", taskNo, e);
            return null;
        }
    }

    /**
     * 构建评估模块列表
     * 从 getReportResponseForm 中提取的核心逻辑
     */
    private List<EvaluationModuleDTO> buildEvaluationModules(
            List<ProcessNode> processNodes,
            List<ModelProcessData> modelProcessDataList,
            List<DataCalling> dataCallings,
            ModelTaskRecord taskRecord) {

        List<EvaluationModuleDTO> modules = new ArrayList<>();
        Map<String, ModelProcessData> processDataMap = modelProcessDataList.stream()
            .collect(Collectors.toMap(ModelProcessData::getModelId, data -> data, (d1, d2) -> d1));

        // 按执行时间排序
        processNodes.sort(Comparator.comparing(
            node -> {
                ModelProcessData data = processDataMap.get(node.getRuleCode());
                return data != null ? data.getCreateTime() : null;
            },
            Comparator.nullsLast(Comparator.naturalOrder())
        ));

        for (ProcessNode node : processNodes) {
            ModelProcessData processData = processDataMap.get(node.getRuleCode());
            if (processData == null) {
                continue;
            }

            List<EvaluationRuleDetailDTO> details = new ArrayList<>();
            BigDecimal calculatedScore = null;

            // 解析引擎响应
            JSONObject responseJson = JSONObject.parseObject(processData.getResponseValue());
            if (responseJson == null || !responseJson.containsKey("data")) {
                continue;
            }
            JSONObject dataObject = responseJson.getJSONObject("data");
            if (dataObject == null || dataObject.isEmpty()) {
                continue;
            }

            // 根据模型类型处理（简化版，只计算分数）
            switch (node.getModuleId()) {
                case 1: // 评分卡
                    calculatedScore = calculateScoreCardScore(dataObject, processData.getModelId());
                    break;
                case 2: // 评级卡
                    if (dataObject.containsKey("score")) {
                        calculatedScore = dataObject.getBigDecimal("score");
                    }
                    break;
                case 3: // 额度卡
                    if (dataObject.containsKey("score")) {
                        calculatedScore = dataObject.getBigDecimal("score");
                    }
                    break;
                case 5: // 规则模型
                    details = extractRuleModelDetails(dataObject);
                    break;
                case 6: // 分类模型
                    details = extractClassificationModelDetails(dataObject, dataCallings);
                    break;
            }

            // 构建模块DTO（简化版）
            if (calculatedScore != null || CollectionUtils.isNotEmpty(details)) {
                EvaluationModuleDTO moduleDTO = new EvaluationModuleDTO();
                moduleDTO.setModelType(mapModelType(node.getModuleId()));
                moduleDTO.setModelId(processData.getModelId());

                if (node.getModuleId() == 1 && calculatedScore != null) {
                    moduleDTO.setFinalScore(calculatedScore);
                }

                moduleDTO.setDetails(details);
                modules.add(moduleDTO);
            }
        }

        return modules;
    }

    /**
     * 计算评分卡分数
     * 修复：即使 codeList 为空，也需要遍历主指标并使用默认规则计算分数
     */
    private BigDecimal calculateScoreCardScore(JSONObject dataObject, String modelId) {
        BigDecimal calculatedScore = BigDecimal.ZERO;
        JSONArray scoreCodeList = dataObject.getJSONArray("codeList");

        // 调试日志：记录输入数据
        log.debug("【评分卡分数计算】开始计算, modelId={}, codeList={}", modelId, scoreCodeList);

        List<Integer> primaryCardById = scoreIndexRuleSnapshotMapper.getPrimaryCardById(modelId);
        log.debug("【评分卡分数计算】主指标数量: {}", primaryCardById != null ? primaryCardById.size() : 0);

        for (Integer primaryCardId : primaryCardById) {
            List<ScoreIndexRuleSnapshot> scoreIndexRuleSnapshots = scoreIndexRuleSnapshotMapper.selectList(
                new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                    .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                    .eq(ScoreIndexRuleSnapshot::getScordCardId, modelId)
                    .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                    .eq(ScoreIndexRuleSnapshot::getDefaultRule, 0)
                    .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                    .orderByAsc(ScoreIndexRuleSnapshot::getLevel)
            );

            boolean flag = false;
            // 只有当 scoreCodeList 不为空时，才尝试匹配非默认规则
            if (CollectionUtils.isNotEmpty(scoreCodeList)) {
                for (ScoreIndexRuleSnapshot scoreIndexRule : scoreIndexRuleSnapshots) {
                    if (scoreCodeList.contains(scoreIndexRule.getIndexRule())) {
                        flag = true;
                        BigDecimal ruleScore = calculateRuleScore(scoreIndexRule);
                        calculatedScore = calculatedScore.add(ruleScore);
                        log.debug("【评分卡分数计算】主指标{}命中规则{}, 规则分数={}", primaryCardId, scoreIndexRule.getIndexRule(), ruleScore);
                        break;
                    }
                }
            }

            // 未命中非默认规则时，使用默认规则
            if (!flag) {
                ScoreIndexRuleSnapshot defaultRule = scoreIndexRuleSnapshotMapper.selectOne(
                    new LambdaQueryWrapper<ScoreIndexRuleSnapshot>()
                        .eq(ScoreIndexRuleSnapshot::getScorePrimaryId, primaryCardId)
                        .eq(ScoreIndexRuleSnapshot::getScordCardId, modelId)
                        .eq(ScoreIndexRuleSnapshot::getButtonState, 1)
                        .eq(ScoreIndexRuleSnapshot::getDefaultRule, 1)
                        .eq(ScoreIndexRuleSnapshot::getDataState, 0)
                        .orderByAsc(ScoreIndexRuleSnapshot::getLevel)
                );
                if (defaultRule != null) {
                    BigDecimal ruleScore = calculateRuleScore(defaultRule);
                    calculatedScore = calculatedScore.add(ruleScore);
                    log.debug("【评分卡分数计算】主指标{}使用默认规则, 规则分数={}", primaryCardId, ruleScore);
                } else {
                    log.debug("【评分卡分数计算】主指标{}未找到默认规则", primaryCardId);
                }
            }
        }

        BigDecimal finalScore = calculatedScore.setScale(2, RoundingMode.HALF_UP);
        log.info("【评分卡分数计算】计算完成, modelId={}, 最终分数={}", modelId, finalScore);
        return finalScore;
    }

    /**
     * 计算单条规则的分数（考虑权重）
     * 使用缓存 Map 避免重复查询数据库
     */
    private BigDecimal calculateRuleScore(ScoreIndexRuleSnapshot scoreIndexRule, Map<String, ScorePrimaryIndexSnapshot> primaryIndexCache) {
        if (scoreIndexRule.getTakeEffect() == 1) {
            return BigDecimal.valueOf(scoreIndexRule.getScore());
        } else {
            String[] primaryIds = scoreIndexRule.getScordPrimaryIds().split(";");
            BigDecimal score = new BigDecimal(scoreIndexRule.getScore());

            for (String primaryId : primaryIds) {
                ScorePrimaryIndexSnapshot scorePrimaryIndex = primaryIndexCache.get(primaryId);
                if (scorePrimaryIndex != null) {
                    score = score.multiply(
                        BigDecimal.valueOf(scorePrimaryIndex.getWeight())
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                    );
                }
            }
            return score;
        }
    }

    /**
     * 计算单条规则的分数（考虑权重）- 重载方法（兼容旧代码）
     * 此方法会实时查询数据库，不推荐在循环中使用
     */
    private BigDecimal calculateRuleScore(ScoreIndexRuleSnapshot scoreIndexRule) {
        if (scoreIndexRule.getTakeEffect() == 1) {
            return BigDecimal.valueOf(scoreIndexRule.getScore());
        } else {
            String[] primaryIds = scoreIndexRule.getScordPrimaryIds().split(";");
            BigDecimal score = new BigDecimal(scoreIndexRule.getScore());

            for (String primaryId : primaryIds) {
                ScorePrimaryIndexSnapshot scorePrimaryIndex = scorePrimaryIndexMapper.selectOne(
                    new LambdaQueryWrapper<ScorePrimaryIndexSnapshot>()
                        .eq(ScorePrimaryIndexSnapshot::getId, primaryId)
                        .eq(ScorePrimaryIndexSnapshot::getDataState, 0)
                        .eq(ScorePrimaryIndexSnapshot::getButtonState, 1)
                );
                if (scorePrimaryIndex != null) {
                    score = score.multiply(
                        BigDecimal.valueOf(scorePrimaryIndex.getWeight())
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                    );
                }
            }
            return score;
        }
    }

    /**
     * 提取规则模型详情
     */
    private List<EvaluationRuleDetailDTO> extractRuleModelDetails(JSONObject dataObject) {
        List<EvaluationRuleDetailDTO> details = new ArrayList<>();
        JSONArray ruleCodeList = dataObject.getJSONArray("codeList");

        if (CollectionUtils.isNotEmpty(ruleCodeList)) {
            for (Object code : ruleCodeList) {
                RdeModelDecisionCodeLevelSnapshot codeLevel = rdeModelDecisionCodeLevelSnapshotMapper.selectOne(
                    new LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot>()
                        .eq(RdeModelDecisionCodeLevelSnapshot::getCode, code.toString())
                        .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0)
                        .last("LIMIT 1")
                );
                if (codeLevel != null) {
                    EvaluationRuleDetailDTO detailDTO = new EvaluationRuleDetailDTO();
                    detailDTO.setRiskLevel(RiskLevelEnum.getDescriptionByCode(Integer.valueOf(codeLevel.getLevel())));
                    detailDTO.setRuleCode(codeLevel.getCode());
                    details.add(detailDTO);
                }
            }
        }

        return details;
    }

    /**
     * 提取分类模型详情
     */
    private List<EvaluationRuleDetailDTO> extractClassificationModelDetails(
            JSONObject dataObject, List<DataCalling> dataCallings) {

        List<EvaluationRuleDetailDTO> details = new ArrayList<>();
        JSONObject jsonObject = dataObject.getJSONObject("jsonObject");

        if (jsonObject == null) {
            return details;
        }

        List<String> classifyCodeList = new ArrayList<>();

        // 从对象中提取（动态遍历所有对象类型接口）
        for (String key : jsonObject.keySet()) {
            // 跳过固定容器字段
            if ("json".equals(key) || "companyModules".equals(key)) {
                continue;
            }

            // 尝试获取对象类型的接口数据
            Object value = jsonObject.get(key);
            if (!(value instanceof JSONObject)) {
                continue;
            }

            JSONObject interfaceData = (JSONObject) value;
            if (interfaceData != null && interfaceData.containsKey("labelAll")) {
                String labelAll = interfaceData.getString("labelAll");
                if (StringUtils.isNotEmpty(labelAll)) {
                    classifyCodeList.addAll(Arrays.asList(labelAll.split(",")));
                }
            }
        }

        // 从数组中提取
        if (jsonObject.containsKey("companyModules")) {
            JSONObject companyModules = jsonObject.getJSONObject("companyModules");
            for (String key : companyModules.keySet()) {
                JSONArray arr = companyModules.getJSONArray(key);
                if (CollectionUtils.isNotEmpty(arr)) {
                    for (int i = 0; i < arr.size(); i++) {
                        JSONObject element = arr.getJSONObject(i);
                        if (element != null && element.containsKey("labelAll")) {
                            String labelAll = element.getString("labelAll");
                            if (StringUtils.isNotEmpty(labelAll)) {
                                classifyCodeList.addAll(Arrays.asList(labelAll.split(",")));
                            }
                        }
                    }
                }
            }
        }

        // 处理提取到的规则
        if (CollectionUtils.isNotEmpty(classifyCodeList)) {
            List<String> distinctCodeList = classifyCodeList.stream().distinct().collect(Collectors.toList());
            for (String code : distinctCodeList) {
                RdeModelDecisionCodeLevelSnapshot codeLevel = rdeModelDecisionCodeLevelSnapshotMapper.selectOne(
                    new LambdaQueryWrapper<RdeModelDecisionCodeLevelSnapshot>()
                        .eq(RdeModelDecisionCodeLevelSnapshot::getCode, code.trim())
                        .eq(RdeModelDecisionCodeLevelSnapshot::getDataStatus, 0)
                        .last("LIMIT 1")
                );
                if (codeLevel != null) {
                    EvaluationRuleDetailDTO detailDTO = new EvaluationRuleDetailDTO();
                    detailDTO.setRuleCode(codeLevel.getCode());

                    // 添加动态数据用于计数
                    List<Map<String, Object>> dynamicData = new ArrayList<>();
                    dynamicData.add(new HashMap<>()); // 占位，用于统计数量
                    detailDTO.setDynamicData(dynamicData);

                    details.add(detailDTO);
                }
            }
        }

        return details;
    }

    /**
     * 构建模型规则结果
     */
    private ModelRuleResultDTO buildModelRuleResult(List<EvaluationModuleDTO> modules) {
        ModelRuleResultDTO modelResults = new ModelRuleResultDTO();

        for (EvaluationModuleDTO module : modules) {
            if ("评分模型".equals(module.getModelType())) {
                JSONObject scoreData = new JSONObject();
                scoreData.put("finalScore", module.getFinalScore());
                modelResults.setScoreData(scoreData);
            } else if ("规则模型".equals(module.getModelType())) {
                JSONObject ruleData = new JSONObject();
                ruleData.put("hitRules", module.getDetails());
                modelResults.setRuleData(ruleData);
            }
        }

        return modelResults;
    }

    private String mapModelType(Integer moduleId) {
        if (moduleId == null) {
            return "未知类型";
        }
        switch (moduleId) {
            case 1: return "评分模型";
            case 2: return "评级模型";
            case 3: return "额度模型";
            case 4: return "定价模型";
            case 5: return "规则模型";
            case 6: return "分类模型";
            default: return "其他模型";
        }
    }
    /**
     * 将风险等级代码映射为决策条件分组
     * @param riskLevelCode 风险等级代码
     * @return 风险等级分组文本
     */
    private String mapRiskLevelToText(String riskLevelCode) {
        if (riskLevelCode == null) {
            return "中风险"; // 默认中风险
        }
        switch (riskLevelCode) {
            case "1": return "低风险";  // 通过
            case "3": return "中风险";  // 预警
            case "5": return "高风险";  // 拒绝
            default: return "中风险";
        }
    }

    /**
     * 填充任务基本信息
     */
    private void fillTaskBasicInfo(EvaluationReportDTO reportDTO, ModelTaskRecord taskRecord, ProcessPolicy processPolicy) {
        // 任务基本信息
        reportDTO.setApplicationUser(taskRecord.getApplicationUser());
        reportDTO.setDeptId(taskRecord.getDeptId());

        // 获取部门名称：优先使用taskRecord中的值，如果为空则通过远程调用获取
        String deptName = taskRecord.getDeptName();
        if (StringUtils.isEmpty(deptName) && taskRecord.getDeptId() != null) {
            deptName = fetchDeptNameByDeptId(Long.valueOf(taskRecord.getDeptId()));
        }
        reportDTO.setDeptName(deptName);

        reportDTO.setBusinessCode(String.valueOf(taskRecord.getBusinessCode()));
        reportDTO.setProcessId(taskRecord.getProcessId());
        reportDTO.setTaskStatus(taskRecord.getTaskStatus());
        reportDTO.setResponseForm(taskRecord.getResponseForm());

        // 任务状态描述映射
        reportDTO.setTaskStatusDesc(mapTaskStatus(taskRecord.getTaskStatus()));

        // 响应形式描述映射
        reportDTO.setResponseFormDesc(mapResponseForm(taskRecord.getResponseForm()));

        // 业务场景描述（从business表查询）
        Business business = businessMapper.selectById(taskRecord.getBusinessCode());
        reportDTO.setBusinessScenario(business != null ? business.getName() : "未知场景");

        // 查询并填充产品信息
        if (processPolicy != null && processPolicy.getProductId() != null) {
            Product product = productMapper.selectById(processPolicy.getProductId());
            if (product != null) {
                reportDTO.setProductName(product.getName());
                reportDTO.setProductId(processPolicy.getProductId());

                // 构建产品详细信息
                EvaluationReportDTO.ProductInfo productInfo = new EvaluationReportDTO.ProductInfo();
                productInfo.setProductName(product.getName());
                productInfo.setProductId(String.valueOf(product.getId()));
                productInfo.setDeptName(reportDTO.getDeptName());
                productInfo.setProductType("自建产品"); // 简化处理
                productInfo.setCreateTime(Date.from(product.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
                // 安全处理updateTime，可能为null
                if (product.getUpdateTime() != null) {
                    productInfo.setUpdateTime(Date.from(product.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant()));
                } else {
                    productInfo.setUpdateTime(productInfo.getCreateTime()); // 使用创建时间作为默认值
                }
                productInfo.setDataStatus(product.getDataStatus() ? "删除" : "正常");
                reportDTO.setProductInfo(productInfo);
            }
        }

        // 构建流程策略详细信息
        if (processPolicy != null) {
            EvaluationReportDTO.ProcessStrategyInfo processStrategyInfo = new EvaluationReportDTO.ProcessStrategyInfo();
            processStrategyInfo.setStrategyName(processPolicy.getProcessStrategy());
            processStrategyInfo.setBusinessScenario(reportDTO.getBusinessScenario());
            processStrategyInfo.setProductName(reportDTO.getProductName());
            processStrategyInfo.setUseStatus(processPolicy.getUseIf() == 1 ? "已启用" : "已禁用");
            processStrategyInfo.setCreateTime(Date.from(processPolicy.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
            // 安全处理updateTime，可能为null
            if (processPolicy.getUpdateTime() != null) {
                processStrategyInfo.setUpdateTime(Date.from(processPolicy.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant()));
            } else {
                processStrategyInfo.setUpdateTime(processStrategyInfo.getCreateTime()); // 使用创建时间作为默认值
            }
            processStrategyInfo.setDescription(processPolicy.getContent());
            reportDTO.setProcessStrategyInfo(processStrategyInfo);
        }
    }

    /**
     * 通过部门ID获取部门名称
     * TODO: 审批流功能已禁用，此方法暂返回null
     *
     * @param deptId 部门ID
     * @return 部门名称，如果获取失败则返回null
     */
    private String fetchDeptNameByDeptId(Long deptId) {
        // TODO: Approval flow feature disabled - FeignRuoYiSystemService removed
        log.debug("fetchDeptNameByDeptId called with deptId: {}, returning null (feature disabled)", deptId);
        return null;
    }

    /**
     * 填充决策结果信息
     */
    private void fillDecisionInfo(EvaluationReportDTO reportDTO, List<EvaluationModuleDTO> modules, Date startTime, Date endTime) {
        EvaluationReportDTO.DecisionInfo decisionInfo = new EvaluationReportDTO.DecisionInfo();

        // 计算实际的模型执行时间范围和总耗时
        Date actualStartTime = startTime;
        Date actualEndTime = endTime;
        Long totalDuration = 0L;

        if (CollectionUtils.isNotEmpty(modules)) {
            // 找到最早的开始时间和最晚的结束时间
            actualStartTime = modules.stream()
                    .map(EvaluationModuleDTO::getStartTime)
                    .filter(time -> time != null)
                    .min(Date::compareTo)
                    .orElse(startTime);

            actualEndTime = modules.stream()
                    .map(EvaluationModuleDTO::getEndTime)
                    .filter(time -> time != null)
                    .max(Date::compareTo)
                    .orElse(endTime);

            // 累加所有模型的实际执行耗时
            totalDuration = modules.stream()
                    .map(EvaluationModuleDTO::getDuration)
                    .filter(duration -> duration != null)
                    .reduce(0L, Long::sum);
        }

        // 基本时间信息
        decisionInfo.setStartTime(actualStartTime);
        decisionInfo.setEndTime(actualEndTime);
        decisionInfo.setTotalDuration(totalDuration);
        decisionInfo.setModelCount(modules.size());

        // 计算综合风险评分（基于所有评分模型的结果）
        Integer totalRiskScore = 0;
        boolean hasScoreModel = false;

        // 按风险等级分组的决策条件
        Map<String, List<String>> decisionConditions = new HashMap<>();
        decisionConditions.put("高风险", new ArrayList<>());
        decisionConditions.put("中风险", new ArrayList<>());
        decisionConditions.put("低风险", new ArrayList<>());
        decisionConditions.put("数据统计", new ArrayList<>());

        // 分析所有模块，提取关键决策信息
        String overallStatus = "通过";
        boolean needReview = false;

        for (EvaluationModuleDTO module : modules) {
            // 处理评分模型
            if ("评分模型".equals(module.getModelType()) && module.getFinalScore() != null) {
                totalRiskScore = module.getFinalScore().intValue();
                hasScoreModel = true;
                // 根据评分确定风险等级
                if (totalRiskScore < 400) {
                    decisionConditions.get("高风险").add("信用评分 = " + totalRiskScore + " 分");
                    needReview = true;
                    overallStatus = "预警";
                } else if (totalRiskScore < 600) {
                    decisionConditions.get("中风险").add("信用评分 = " + totalRiskScore + " 分");
                } else {
                    decisionConditions.get("低风险").add("信用评分 = " + totalRiskScore + " 分");
                }
            }

            // 处理规则模型
            if ("规则模型".equals(module.getModelType()) && CollectionUtils.isNotEmpty(module.getDetails())) {
                for (EvaluationRuleDetailDTO detail : module.getDetails()) {
                    String riskLevel = mapRiskLevelToText(detail.getRiskLevel());
                    String condition = detail.getHitAction();
                    if (StringUtils.isNotEmpty(condition)) {
                        decisionConditions.get(riskLevel).add(condition);
                        if ("高风险".equals(riskLevel)) {
                            needReview = true;
                            overallStatus = "预警";
                        }
                    }
                }
            }

            // 处理分类模型 - 统计信息
            if ("分类模型".equals(module.getModelType()) && CollectionUtils.isNotEmpty(module.getDetails())) {
                module.getDetails().stream()
                        .filter(d -> "ClassificationResult".equals(d.getRuleCode()))
                        .forEach(d -> {
                            String interfaceName = d.getHitAction().replaceAll("接口 \\[", "").replaceAll("\\].*", "");
                            int dataSize = (d.getDynamicData() != null) ? d.getDynamicData().size() : 0;
                            decisionConditions.get("数据统计").add(interfaceName + ": " + dataSize + " 条");

                            // 尝试进行更细致的统计，比如按caseTypeNew
                            if (dataSize > 0) {
                                try {
                                    Map<String, Long> caseTypeStats = d.getDynamicData().stream()
                                            .map(item -> (String) item.get("caseTypeNew"))
                                            .filter(StringUtils::isNotEmpty)
                                            .collect(Collectors.groupingBy(
                                                    item -> item,
                                                    Collectors.counting()
                                            ));
                                    caseTypeStats.forEach((caseType, count) -> {
                                        decisionConditions.get("数据统计").add("  - " + caseType + ": " + count + " 条");
                                    });
                                } catch (Exception e) {
                                    // 如果数据结构不匹配（例如没有caseTypeNew字段），则忽略这个细分统计
                                    log.warn("无法按caseTypeNew对接口 {} 的数据进行统计", interfaceName);
                                }
                            }
                        });
            }
        }
        // 清理空的分组
        decisionConditions.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        // 设置决策结果
        decisionInfo.setRiskScore(hasScoreModel ? totalRiskScore : null);
        decisionInfo.setFinalStatus(overallStatus);
        decisionInfo.setReviewStatus(needReview ? "待人工复核" : "无需复核");
        decisionInfo.setNeedManualReview(needReview ? "是" : "否");
        decisionInfo.setDecisionConditions(decisionConditions);

        // 简化的决策代码和等级
        if (hasScoreModel) {
            if (totalRiskScore >= 600) {
                decisionInfo.setFinalDecisionCode("PASS-001");
                decisionInfo.setDecisionLevel("通过-低风险");
            } else {
                decisionInfo.setFinalDecisionCode("WARN-001");
                decisionInfo.setDecisionLevel("预警-中风险");
            }
        } else {
            decisionInfo.setFinalDecisionCode("REVIEW-001");
            decisionInfo.setDecisionLevel("待复核");
        }

        decisionInfo.setRiskType("综合风险");
        decisionInfo.setDecisionDescription("基于" + modules.size() + "个模型的综合评估结果");

        reportDTO.setDecisionInfo(decisionInfo);
    }

    /**
     * 映射任务状态
     */
    private String mapTaskStatus(Integer taskStatus) {
        if (taskStatus == null) return "未知状态";
        switch (taskStatus) {
            case 1: return "初始化";
            case 2: return "生成中";
            case 3: return "生成成功";
            case 4: return "生成失败";
            default: return "未知状态";
        }
    }

    /**
     * 映射响应形式
     */
    private String mapResponseForm(Integer responseForm) {
        if (responseForm == null) return "未知形式";
        switch (responseForm) {
            case 1: return "数据";
            case 2: return "报告";
            default: return "未知形式";
        }
    }

    /**
     * 根据与任务关联的所有数据调用记录，获取动态表头。
     *
     * @param dataCallings 当前任务的所有数据中台调用记录
     * @return 动态表头列表，每个表头是一个包含 "field" 和 "fieldName" 的 Map
     */
    private List<Map<String, String>> getHeadersForTask(List<DataCalling> dataCallings) {
        if (CollectionUtils.isEmpty(dataCallings)) {
            return Collections.emptyList();
        }

        // 1. 提取所有唯一的 manageNo
        Set<Object> manageNos = dataCallings.stream()
                .map(DataCalling::getManageNo)
                .filter(StringUtils::isNotNull)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(manageNos)) {
            return Collections.emptyList();
        }

        // 2. 遍历 manageNo，调用中台服务获取字段信息，并收集所有中文字段名
        // 使用 Map 来收集字段信息，key 为英文字段名，value 为中文字段描述，以实现去重
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (Object manageNo : manageNos) {
            try {
                FindInterfaceFieldIdInfoDTO dto = new FindInterfaceFieldIdInfoDTO();
                dto.setManageNo((String) manageNo);
                AjaxResult result = feignDataMiddleStationService.findInterfaceFieldIdInfo(dto);

                if (result != null && result.get("data") != null) {
                    Object data = result.get("data");
                    JSONObject dataObject = JSON.parseObject(JSON.toJSONString(data));
                    JSONArray list = dataObject.getJSONArray("list");

                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            if (item != null && item.getInteger("interfaceFieldIdType") == 1) {
                                String fieldName = item.getString("interfaceFieldIdName");
                                String fieldDescription = item.getString("interfaceFieldIdDescription");

                                // 只有当英文字段名和中文字段描述都存在时才添加
                                if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(fieldDescription)) {
                                    // put 方法会自动覆盖重复的 key，实现去重
                                    headerMap.put(fieldName, fieldDescription);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("通过 manageNo [{}] 获取表头信息失败", manageNo, e);
            }
        }

        // 3. 将 Map 转换为前端所需的 List<Map<String, String>> 格式
        return headerMap.entrySet().stream()
                .map(entry -> {
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("field", entry.getKey());
                    fieldMap.put("fieldName", entry.getValue());
                    return fieldMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * 处理动态数据并创建 EvaluationRuleDetailDTO
     */
    private void processDynamicData(List<EvaluationRuleDetailDTO> details, String interfaceName, JSONArray dynamicData, List<DataCalling> dataCallings) {
        // 为动态数据表格创建一个单独的 detail DTO
        EvaluationRuleDetailDTO dataDetailDTO = new EvaluationRuleDetailDTO();
        dataDetailDTO.setRuleCode(interfaceName); // 使用接口名作为标识
        dataDetailDTO.setHitAction("接口 [" + interfaceName + "] 返回 " + dynamicData.size() + " 条数据");

        List<Map<String, Object>> typedDynamicData = new ArrayList<>();
        for (Object item : dynamicData) {
            if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> typedItem = (Map<String, Object>) item;
                typedDynamicData.add(typedItem);
            }
        }
        dataDetailDTO.setDynamicData(typedDynamicData);

        // 根据实际数据对表头进行排序和过滤
        if (CollectionUtils.isNotEmpty(typedDynamicData)) {
            List<Map<String, String>> allHeaders = getHeadersForTask(dataCallings);
            Map<String, String> headerMap = allHeaders.stream()
                    .collect(Collectors.toMap(h -> h.get("field"), h -> h.get("fieldName"), (v1, v2) -> v1));

            Set<String> dataFields = typedDynamicData.get(0).keySet();
            List<Map<String, String>> finalHeaders = new ArrayList<>();

            for (String field : dataFields) {
                if (headerMap.containsKey(field)) {
                    Map<String, String> finalHeader = new HashMap<>();
                    finalHeader.put("field", field);
                    finalHeader.put("fieldName", headerMap.get(field));
                    finalHeaders.add(finalHeader);
                }
            }
            dataDetailDTO.setDynamicHeaders(finalHeaders);
        } else {
            dataDetailDTO.setDynamicHeaders(getHeadersForTask(dataCallings));
        }
        details.add(dataDetailDTO);
    }

    /**
     * 计算授信额度 - Service层方法
     * 从Controller层的quota方法提取的业务逻辑
     *
     * @param cname               企业名称
     * @param userIdentification  加密的用户标识
     * @param orderNo             请求唯一流水号
     * @return Map包含creditFacility(授信额度)和description(计算依据)
     * @throws Exception 解密或API调用失败时抛出异常
     */
    private Map<String, Object> calculateCreditFacilityFromFinancialData(String cname, String userIdentification, String orderNo) throws Exception {
        // 1. 参数校验
        if (cn.hutool.core.util.StrUtil.isBlank(cname) || cn.hutool.core.util.StrUtil.isBlank(userIdentification) || cn.hutool.core.util.StrUtil.isBlank(orderNo)) {
            throw new IllegalArgumentException("cname、userIdentification、orderNo不能为空");
        }


        Long userId = Long.valueOf(userIdentification);

        // 3. 根据userId获取数据中台密钥
        com.risksmart.common.core.domain.R<com.value.decision.process.vo.InterfaceUser> interfaceUserR =
                feignDataMiddleStationService.queryAppKeyByUserId(userId.intValue());
        com.value.decision.process.vo.InterfaceUser interfaceUser = interfaceUserR.getData();

        if (interfaceUser == null) {
            String errorMsg = String.format("未找到用户%s的数据中台密钥,code:%s,msg:%s",
                    userId, interfaceUserR.getCode(), interfaceUserR.getMsg());
            log.error("【额度计算】{}", errorMsg);
            throw new RuntimeException(errorMsg);
        }

        // 4. 构建请求参数并调用数据中台API获取财务数据
        JSONObject params = new JSONObject();
        params.put("cname", cname);
        params.put("userIdentification", userId);
        params.put("orderNo", orderNo);


        EncryptDTO encryptBody = EncryptBodyUtil.createEncryptBody(
                interfaceUser.getAppKey(),
                interfaceUser.getSecret(),
                properties.getQuota().getManageNo(),
                properties.getQuota().getSourceNo(),
                properties.getQuota().getInterfaceNo(),
                orderNo,
                params
        );

        JSONObject result = feignDataMiddleStationService.api(encryptBody);
        log.debug("【额度计算】数据中台返回结果: {}", result.toJSONString());

        // 5. 根据公式计算授信额度
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("creditFacility", BigDecimal.ZERO);

        // 获取data字段 (数据中台直接返回最新财务数据)
        Object dataObj = result.get("data");
        if (dataObj == null) {
            log.warn("【额度计算】财务数据为空：{}", result.getString("msg"));
            return resultMap;
        }

        // 将data转换为JSONObject (兼容FastJSON返回的LinkedHashMap)
        JSONObject financialData;
        if (dataObj instanceof Map) {
            log.debug("【额度计算】检测到Map类型({}),转换为JSONObject", dataObj.getClass().getName());
            financialData = new JSONObject((Map<String, Object>) dataObj);
        } else if (dataObj instanceof JSONObject) {
            financialData = (JSONObject) dataObj;
        } else {
            log.warn("【额度计算】data类型不支持: {}", dataObj.getClass().getName());
            return resultMap;
        }

        // 检查是否包含必要的财务字段
        if (!financialData.containsKey("cFinanceTotalOwnersEquity") && !financialData.containsKey("cfinanceTotalOwnersEquity")) {
            log.warn("【额度计算】财务数据缺少必要字段cFinanceTotalOwnersEquity");
            return resultMap;
        }

        log.debug("【额度计算】获取到最新财务数据，准备计算授信额度");

        // 6. 计算授信额度
        BigDecimal creditFacility = calculateCreditFacilityFormula(financialData);
        resultMap.put("creditFacility", creditFacility);
        resultMap.put("description", "最新财务数据");

        log.info("【额度计算】计算成功，企业：{}，授信额度：{}", cname, creditFacility);

        return resultMap;
    }

    private BigDecimal getFinancialField(JSONObject data, String lowercaseKey, String camelCaseKey) {
        BigDecimal value = data.getBigDecimal(camelCaseKey);
        return value != null ? value : data.getBigDecimal(lowercaseKey);
    }

    /**
     * 计算授信额度公式
     * 公式如下：
     * IF(
     *     cFinanceTotalOwnersEquity <= 0,
     *     0,
     *     MIN(
     *         cFinanceMainBusinessRevenue * 0.5,
     *         MAX(0, cFinanceNetProfit) * 7,
     *         cFinanceTotalOwnersEquity * 0.5
     *     ) * MAX(
     *         0,
     *         1 - 2 * MAX(0, cFinanceAssetLiabilityRatio - 0.5)
     *     )
     * )
     *
     * @param financialData 财务年报数据
     * @return 授信额度，保留2位小数
     */
    private BigDecimal calculateCreditFacilityFormula(JSONObject financialData) {
        // 获取财务数据 (兼容大小写不同的字段名)
        BigDecimal cFinanceTotalOwnersEquity = getFinancialField(financialData,
                "cfinanceTotalOwnersEquity", "cFinanceTotalOwnersEquity");
        BigDecimal cFinanceMainBusinessRevenue = getFinancialField(financialData,
                "cfinanceMainBusinessRevenue", "cFinanceMainBusinessRevenue");
        BigDecimal cFinanceNetProfit = getFinancialField(financialData,
                "cfinanceNetProfit", "cFinanceNetProfit");
        BigDecimal cFinanceAssetLiabilityRatio = getFinancialField(financialData,
                "cfinanceAssetLiabilityRatio", "cFinanceAssetLiabilityRatio");

        // 处理 null 值情况
        if (cFinanceTotalOwnersEquity == null || cFinanceMainBusinessRevenue == null ||
                cFinanceNetProfit == null || cFinanceAssetLiabilityRatio == null) {
            log.warn("【额度计算】财务数据字段存在null值，返回0");
            return BigDecimal.ZERO;
        }

        // 资产负债率单位转换: 如果大于1,说明是百分比数值(如66.54),需要除以100转为小数(如0.6654)
        if (cFinanceAssetLiabilityRatio.compareTo(BigDecimal.ONE) > 0) {
            cFinanceAssetLiabilityRatio = cFinanceAssetLiabilityRatio.divide(QuotaCalculationConstants.PERCENT_DIVISOR, QuotaCalculationConstants.SCALE_PRECISION, RoundingMode.HALF_UP);
            log.debug("【额度计算】资产负债率单位转换: {} → {}", cFinanceAssetLiabilityRatio.multiply(QuotaCalculationConstants.PERCENT_DIVISOR), cFinanceAssetLiabilityRatio);
        }

        // 1. 如果所有者权益 <= 0，返回 0
        if (cFinanceTotalOwnersEquity.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("【额度计算】所有者权益为负，返回0");
            return BigDecimal.ZERO;
        }

        log.debug("【额度计算】财务数据 - 所有者权益:{}, 营业收入:{}, 净利润:{}, 资产负债率:{}",
                cFinanceTotalOwnersEquity, cFinanceMainBusinessRevenue, cFinanceNetProfit, cFinanceAssetLiabilityRatio);

        // 2. 计算 MIN 部分
        BigDecimal revenuePart = cFinanceMainBusinessRevenue.multiply(QuotaCalculationConstants.REVENUE_FACTOR);
        BigDecimal netProfitPart = cFinanceNetProfit.max(BigDecimal.ZERO).multiply(QuotaCalculationConstants.PROFIT_FACTOR);
        BigDecimal equityPart = cFinanceTotalOwnersEquity.multiply(QuotaCalculationConstants.EQUITY_FACTOR);

        log.debug("【额度计算】MIN(营业收入*0.5={}, 净利润*7={}, 所有者权益*0.5={}) = {}",
                revenuePart, netProfitPart, equityPart, revenuePart.min(netProfitPart).min(equityPart));

        // 取三者中的最小值
        BigDecimal minPart = revenuePart.min(netProfitPart).min(equityPart);

        // 3. 计算调节因子 MAX(0, 1 - 2 * MAX(0, 资产负债率 - 0.5))
        BigDecimal ratioExcess = cFinanceAssetLiabilityRatio.subtract(QuotaCalculationConstants.RATIO_THRESHOLD).max(BigDecimal.ZERO);
        BigDecimal adjustmentFactor = BigDecimal.ONE
                .subtract(QuotaCalculationConstants.RATIO_ADJUSTMENT.multiply(ratioExcess))
                .max(BigDecimal.ZERO);

        log.debug("【额度计算】调节因子 = MAX(0, 1 - 2*{}) = {}", ratioExcess, adjustmentFactor);

        // 4. 最终结果 = minPart * adjustmentFactor
        BigDecimal result = minPart.multiply(adjustmentFactor);
        log.debug("【额度计算】最终结果 = {} * {} = {}", minPart, adjustmentFactor, result);

        // 5. 保留两位小数并四舍五入
        return result.setScale(QuotaCalculationConstants.RESULT_SCALE, RoundingMode.HALF_UP);
    }
}
