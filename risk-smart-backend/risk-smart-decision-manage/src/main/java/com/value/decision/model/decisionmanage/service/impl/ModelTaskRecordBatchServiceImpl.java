package com.value.decision.model.decisionmanage.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.utils.SnowFlakeCloud;
import com.value.decision.model.decisionmanage.mapper.ModelTaskRecordBatchMapper;
import com.value.decision.model.decisionmanage.mapper.ModelTaskRecordMapper;
import com.value.decision.model.decisionmanage.model.ModelTaskRecord;
import com.value.decision.model.decisionmanage.model.ModelTaskRecordBatch;
import com.value.decision.process.mapper.ProcessNodeMapper;
import com.value.decision.model.decisionmanage.mapper.ScoreCardRecordSnapshotMapper;
import com.value.decision.model.decisionmanage.model.ScoreCardRecordSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import com.value.decision.snapshot.mapper.RdeModelAntiFraudSnapshotMapper;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchDetailVO;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchQueryDTO;
import com.value.decision.model.decisionmanage.model.dto.batch.ValidateResult;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestDTO;
import com.value.decision.model.decisionmanage.model.dto.model.PolicyRequestVO;
import com.value.decision.model.decisionmanage.service.IModelTaskRecordBatchService;
import com.value.decision.model.decisionmanage.service.IModelTaskRecordService;
import com.value.decision.model.decisionmanage.service.IFailureLogService;
import com.value.decision.model.decisionmanage.enums.FailureSceneEnum;
import com.value.decision.process.model.ProcessNode;
import com.value.decision.process.model.ProcessPolicy;
import com.value.decision.process.service.IProcessPolicyService;
import com.risksmart.common.core.web.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ModelTaskRecordBatchServiceImpl
        extends ServiceImpl<ModelTaskRecordBatchMapper, ModelTaskRecordBatch>
        implements IModelTaskRecordBatchService {

    @Autowired
    private ModelTaskRecordBatchMapper batchMapper;

    @Autowired
    private ModelTaskRecordMapper taskRecordMapper;

    @Autowired
    private IModelTaskRecordService modelTaskRecordService;

    @Autowired
    private IProcessPolicyService processPolicyService;

    @Autowired
    private ProcessNodeMapper processNodeMapper;

    @Autowired
    private ScoreCardRecordSnapshotMapper scoreCardRecordSnapshotMapper;

    @Autowired
    private RdeModelAntiFraudSnapshotMapper rdeModelAntiFraudSnapshotMapper;

    @Autowired
    private IFailureLogService failureLogService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取 Spring AOP 代理对象（懒加载方式，避免循环依赖）
     * 用于确保 @Async 注解生效
     */
    private IModelTaskRecordBatchService getSelf() {
        return applicationContext.getBean(IModelTaskRecordBatchService.class);
    }

    // 文件大小限制: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 数据行数限制: 1000行
    private static final int MAX_ROW_COUNT = 1000;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> uploadFile(MultipartFile file, Integer processId, Integer deptId) {
        log.info("开始处理文件上传: processId={}, fileName={}", processId, file.getOriginalFilename());

        // 1. 文件基础校验
        ValidateResult fileValidation = validateFile(file);
        if (!fileValidation.isSuccess()) {
            throw new RuntimeException(fileValidation.getErrorMessage());
        }

        // 2. 获取文件信息（不再上传到外部服务器，直接处理）
        String originalFilename = file.getOriginalFilename();
        // 使用时间戳生成唯一文件标识
        String fileUrl = "batch_" + System.currentTimeMillis() + "_" + originalFilename;
        log.info("文件处理成功: fileUrl={}", fileUrl);

        // 3. 读取Excel数据
        List<Map<String, Object>> dataList;
        try {
            dataList = readExcelData(file, processId, deptId);
        } catch (Exception e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("Excel文件读取失败: " + e.getMessage());
        }

        // 4. 数据校验
        ValidateResult dataValidation = validateBatchData(dataList, processId, deptId);
        if (!dataValidation.isSuccess()) {
            throw new RuntimeException(dataValidation.getErrorMessage());
        }

        // 5. 返回文件信息(不创建批次,不执行任务)
        Map<String, Object> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        result.put("fileName", originalFilename);
        result.put("totalCount", dataList.size());
        result.put("fileSize", file.getSize());

        log.info("文件上传校验完成: fileUrl={}, totalCount={}", fileUrl, dataList.size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitBatch(String fileUrl, String fileName, Integer processId,
                                          Integer responseForm, Integer userId,
                                          String userName, Integer deptId) {
        log.info("开始提交批次任务: processId={}, userId={}, fileName={}, fileUrl={}",
                processId, userId, fileName, fileUrl);

        // 1. 参数校验
        if (StrUtil.isBlank(fileUrl)) {
            throw new RuntimeException("文件URL不能为空");
        }
        if (StrUtil.isBlank(fileName)) {
            throw new RuntimeException("文件名不能为空");
        }

        // 2. 从fileUrl下载并读取Excel数据
        List<Map<String, Object>> dataList;
        try {
            dataList = readExcelDataFromUrl(fileUrl, processId, deptId);
        } catch (Exception e) {
            log.error("从fileUrl读取数据失败: fileUrl={}", fileUrl, e);
            throw new RuntimeException("读取数据失败: " + e.getMessage());
        }

        // 3. 数据校验
        ValidateResult dataValidation = validateBatchData(dataList, processId, deptId);
        if (!dataValidation.isSuccess()) {
            throw new RuntimeException(dataValidation.getErrorMessage());
        }

        // 4. 预先生成所有任务号
        List<String> taskNos = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            String taskNo = "MTask_" + SnowFlakeCloud.nextId();
            taskNos.add(taskNo);
        }

        // 5. 创建批次记录(状态=1待处理,包含fileUrl)
        String batchNo = "MBatch_" + SnowFlakeCloud.nextId();
        ModelTaskRecordBatch batch = createBatch(batchNo, processId, responseForm,
                userId, userName, deptId, fileName, fileUrl, dataList.size());

        log.info("批次创建成功: batchNo={}, batchId={}, totalCount={}, fileUrl={}",
                batchNo, batch.getId(), dataList.size(), fileUrl);

        // 6. 异步处理批量任务(传入预先生成的任务号列表)
        // 关键修复: 通过代理对象调用，确保 @Async 注解生效
        getSelf().processBatchAsync(batch.getId(), dataList, taskNos, processId, responseForm, userId, deptId);

        // 7. 返回批次信息(包含任务号数组)
        Map<String, Object> result = new HashMap<>();
        result.put("batchNo", batchNo);
        result.put("batchId", batch.getId());
        result.put("totalCount", dataList.size());
        result.put("taskNos", taskNos);
        return result;
    }


    @Override
    @Async("batchTaskExecutor")
    public void processBatchAsync(Long batchId, List<Map<String, Object>> dataList, List<String> taskNos,
                                  Integer processId, Integer responseForm,
                                  Integer userId, Integer deptId) {
        log.info("开始异步处理批次: batchId={}, totalCount={}", batchId, dataList.size());

        try {
            // 1. 更新批次状态为处理中
            ModelTaskRecordBatch batch = batchMapper.selectById(batchId);
            if (batch == null) {
                log.error("批次不存在: batchId={}", batchId);
                return;
            }

            batch.setBatchStatus(2); // 处理中
            batch.setStartTime(LocalDateTime.now());
            batchMapper.updateById(batch);

            // 2. 查询流程节点和模型名称快照(批次中所有任务共享)
            LambdaQueryWrapper<ProcessNode> wrapperProcessNode = Wrappers.lambdaQuery();
            wrapperProcessNode.eq(ProcessNode::getProcessStrategyId, processId)
                    .eq(ProcessNode::getDeptId, deptId)
                    .eq(ProcessNode::getDataStatus, 0);
            List<ProcessNode> processNodeList = processNodeMapper.selectList(wrapperProcessNode);

            // 获取模型名称快照
            String modelNameSnapshot = getModelNameSnapshot(processNodeList);

            int successCount = 0;
            int failCount = 0;

            // 3. 遍历处理每条数据
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);

                try {
                    // 3.1 使用预先生成的任务号
                    String taskNo = taskNos.get(i);

                    // 3.2 构建任务参数
                    ModelTaskRecordVO taskVO = buildTaskVO(taskNo, rowData, processId,
                            responseForm, userId, deptId);

                    // 3.3 设置applicationUser(从batch获取)
                    taskVO.setApplicationUser(batch.getApplicationUser());

                    // 3.4 创建任务记录 (传入processNodeList和modelNameSnapshot)
                    ModelTaskRecord taskRecord = createTaskRecord(taskVO, batch, processNodeList, modelNameSnapshot);

                    // 3.5 执行任务(不创建记录,仅执行流程)
                    try {
                        modelTaskRecordService.executeProcessFlowForBatch(taskVO);
                        successCount++;

                        // 更新任务状态为成功
                        taskRecord.setTaskStatus(3);
                        taskRecordMapper.updateById(taskRecord);

                    } catch (Exception e) {
                        failCount++;
                        log.error("批次任务执行失败: batchId={}, taskNo={}, index={}",
                                batchId, taskNo, i + 3, e);

                        // 同步记录失败日志，确保时序性和数据准确性
                        FailureSceneEnum scene = FailureSceneEnum.inferFromException(e);
                        Map<String, Object> context = new HashMap<>();
                        context.put("batchId", batchId);
                        context.put("processId", processId);
                        failureLogService.logFailureSync(taskNo, scene, e);

                        // 更新任务状态为失败
                        taskRecord.setTaskStatus(4);
                        taskRecordMapper.updateById(taskRecord);
                    }

                } catch (Exception e) {
                    failCount++;
                    log.error("批次任务创建失败: batchId={}, index={}", batchId, i + 3, e);
                }

                // 每处理10条,打印进度日志
                if ((i + 1) % 10 == 0) {
                    log.info("批次处理进度: batchId={}, processed={}/{}, success={}, fail={}",
                            batchId, i + 1, dataList.size(), successCount, failCount);
                }
            }

            // 3. 更新批次统计和状态
            batch.setSuccessCount(successCount);
            batch.setFailCount(failCount);
            batch.setFinishTime(LocalDateTime.now());

            // 确定最终状态
            if (failCount == 0) {
                batch.setBatchStatus(3); // 全部成功
            } else if (successCount == 0) {
                batch.setBatchStatus(5); // 全部失败
            } else {
                batch.setBatchStatus(4); // 部分失败
            }

            batchMapper.updateById(batch);

            log.info("批次处理完成: batchId={}, 成功={}, 失败={}, 状态={}, 耗时={}ms",
                    batchId, successCount, failCount, batch.getBatchStatus(),
                    java.time.Duration.between(batch.getStartTime(), batch.getFinishTime()).toMillis());

        } catch (Exception e) {
            log.error("批次处理异常: batchId={}", batchId, e);

            // 更新批次状态为失败
            try {
                ModelTaskRecordBatch batch = batchMapper.selectById(batchId);
                batch.setBatchStatus(5); // 全部失败
                batch.setErrorMessage("批次处理异常: " + e.getMessage());
                batch.setFinishTime(LocalDateTime.now());
                batchMapper.updateById(batch);
            } catch (Exception updateException) {
                log.error("更新批次状态失败: batchId={}", batchId, updateException);
            }
        }
    }

    @Override
    public List<ModelTaskRecordBatch> getBatchList(BatchQueryDTO queryDTO) {
        LambdaQueryWrapper<ModelTaskRecordBatch> wrapper = Wrappers.lambdaQuery();

        // 批次号模糊查询
        if (StrUtil.isNotBlank(queryDTO.getBatchNo())) {
            wrapper.like(ModelTaskRecordBatch::getBatchNo, queryDTO.getBatchNo());
        }

        // 流程策略ID
        if (queryDTO.getProcessId() != null) {
            wrapper.eq(ModelTaskRecordBatch::getProcessId, queryDTO.getProcessId());
        }

        // 流程策略名称模糊查询
        if (StrUtil.isNotBlank(queryDTO.getProcessStrategy())) {
            wrapper.like(ModelTaskRecordBatch::getProcessStrategy, queryDTO.getProcessStrategy());
        }

        // 批次状态
        if (queryDTO.getBatchStatus() != null) {
            wrapper.eq(ModelTaskRecordBatch::getBatchStatus, queryDTO.getBatchStatus());
        }

        // 响应形式
        if (queryDTO.getResponseForm() != null) {
            wrapper.eq(ModelTaskRecordBatch::getResponseForm, queryDTO.getResponseForm());
        }

        // 创建时间范围
        if (StrUtil.isNotBlank(queryDTO.getStartTime())) {
            wrapper.ge(ModelTaskRecordBatch::getCreateTime, queryDTO.getStartTime());
        }
        if (StrUtil.isNotBlank(queryDTO.getEndTime())) {
            wrapper.le(ModelTaskRecordBatch::getCreateTime, queryDTO.getEndTime());
        }

        // 部门ID(必填)
        if (queryDTO.getDeptId() != null) {
            wrapper.eq(ModelTaskRecordBatch::getDeptId, queryDTO.getDeptId());
        }

        // 用户ID
        if (queryDTO.getUserId() != null) {
            wrapper.eq(ModelTaskRecordBatch::getUserId, queryDTO.getUserId());
        }

        // 用户名模糊查询
        if (StrUtil.isNotBlank(queryDTO.getUserName())) {
            wrapper.like(ModelTaskRecordBatch::getApplicationUser, queryDTO.getUserName());
        }

        // 数据状态
        wrapper.eq(ModelTaskRecordBatch::getDataStatus, 0);

        // 按创建时间降序
        wrapper.orderByDesc(ModelTaskRecordBatch::getCreateTime);

        List<ModelTaskRecordBatch> batchList = list(wrapper);

        // 填充真实的modelName和modelNameList字段
        for (ModelTaskRecordBatch batch : batchList) {
            // 查询该批次下的第一条任务记录,获取processNodesSnapshot
            LambdaQueryWrapper<ModelTaskRecord> taskWrapper = Wrappers.lambdaQuery();
            taskWrapper.eq(ModelTaskRecord::getBatchId, batch.getId())
                    .eq(ModelTaskRecord::getDataStatus, 0)
                    .last("LIMIT 1");
            ModelTaskRecord firstTask = taskRecordMapper.selectOne(taskWrapper);

            if (firstTask != null && StrUtil.isNotBlank(firstTask.getProcessNodesSnapshot())) {
                // 从快照中解析流程节点
                try {
                    List<ProcessNode> processNodeList = JSON.parseArray(firstTask.getProcessNodesSnapshot(), ProcessNode.class);

                    // 收集模型ID
                    Set<String> scoreRuleCodes = new HashSet<>();
                    Set<String> ruleOrSortRuleCodes = new HashSet<>();
                    for (ProcessNode node : processNodeList) {
                        if (StrUtil.isNotEmpty(node.getRuleCode())) {
                            if (node.getModuleId() == 1) {
                                scoreRuleCodes.add(node.getRuleCode());
                            } else if (node.getModuleId() == 5 || node.getModuleId() == 6) {
                                ruleOrSortRuleCodes.add(node.getRuleCode());
                            }
                        }
                    }

                    // 查询真实的模型名称
                    List<String> modelNames = new ArrayList<>();

                    if (CollectionUtils.isNotEmpty(scoreRuleCodes)) {
                        List<ScoreCardRecordSnapshot> scoreCards = scoreCardRecordSnapshotMapper.selectList(
                                Wrappers.lambdaQuery(ScoreCardRecordSnapshot.class)
                                        .in(ScoreCardRecordSnapshot::getId, scoreRuleCodes)
                                        .eq(ScoreCardRecordSnapshot::getDataState, 0)
                        );
                        for (ScoreCardRecordSnapshot card : scoreCards) {
                            modelNames.add(card.getScoreCard());
                        }
                    }

                    if (CollectionUtils.isNotEmpty(ruleOrSortRuleCodes)) {
                        List<RdeModelAntiFraudSnapshot> antiFrauds = rdeModelAntiFraudSnapshotMapper.selectList(
                                Wrappers.lambdaQuery(RdeModelAntiFraudSnapshot.class)
                                        .in(RdeModelAntiFraudSnapshot::getId, ruleOrSortRuleCodes)
                                        .eq(RdeModelAntiFraudSnapshot::getDataStatus, 0)
                        );
                        for (RdeModelAntiFraudSnapshot fraud : antiFrauds) {
                            modelNames.add(fraud.getName());
                        }
                    }

                    // 设置真实的模型名称
                    if (CollectionUtils.isNotEmpty(modelNames)) {
                        batch.setModelName(String.join(",", modelNames));
                        batch.setModelNameList(modelNames);
                    } else {
                        // 如果没有找到,使用原有的modelName
                        batch.setModelNameList(StrUtil.isNotBlank(batch.getModelName())
                                ? Arrays.asList(batch.getModelName().split(","))
                                : Collections.emptyList());
                    }
                } catch (Exception e) {
                    log.warn("解析批次模型名称失败: batchId={}", batch.getId(), e);
                    // 解析失败,使用原有的modelName
                    batch.setModelNameList(StrUtil.isNotBlank(batch.getModelName())
                            ? Arrays.asList(batch.getModelName().split(","))
                            : Collections.emptyList());
                }
            } else {
                // 没有找到任务记录,使用原有的modelName
                batch.setModelNameList(StrUtil.isNotBlank(batch.getModelName())
                        ? Arrays.asList(batch.getModelName().split(","))
                        : Collections.emptyList());
            }
        }

        return batchList;
    }

    @Override
    public BatchDetailVO getBatchDetail(Long batchId, Integer pageNum, Integer pageSize) {
        // 1. 查询批次信息
        ModelTaskRecordBatch batch = batchMapper.selectById(batchId);
        if (batch == null) {
            return null;
        }

        // 2. 分页查询任务列表
        PageHelper.startPage(pageNum, pageSize);
        LambdaQueryWrapper<ModelTaskRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelTaskRecord::getBatchId, batchId)
                .eq(ModelTaskRecord::getDataStatus, 0)
                .orderByAsc(ModelTaskRecord::getCreateTime); // 使用create_time排序

        List<ModelTaskRecord> taskList = taskRecordMapper.selectList(wrapper);
        PageInfo<ModelTaskRecord> pageInfo = new PageInfo<>(taskList);

        // 3. 组装返回数据
        BatchDetailVO detailVO = new BatchDetailVO();
        detailVO.setBatch(batch);
        detailVO.setTaskList(pageInfo.getList());
        detailVO.setTotal(pageInfo.getTotal());

        return detailVO;
    }

    /**
     * 文件校验
     */
    private ValidateResult validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ValidateResult.fail("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".xlsx")) {
            return ValidateResult.fail("仅支持.xlsx格式的Excel文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return ValidateResult.fail("文件大小不能超过10MB");
        }

        return ValidateResult.success();
    }

    /**
     * 读取Excel数据(从MultipartFile)
     */
    private List<Map<String, Object>> readExcelData(MultipartFile file, Integer processId, Integer deptId)
            throws IOException {
        return readExcelDataFromInputStream(file.getInputStream(), processId, deptId);
    }

    /**
     * 从fileUrl下载并读取Excel数据
     */
    private List<Map<String, Object>> readExcelDataFromUrl(String fileUrl, Integer processId, Integer deptId)
            throws IOException {
        log.info("开始从URL下载文件: fileUrl={}", fileUrl);

        InputStream inputStream = null;
        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(60000);    // 60秒读取超时
            inputStream = connection.getInputStream();

            return readExcelDataFromInputStream(inputStream, processId, deptId);

        } catch (Exception e) {
            log.error("从URL下载文件失败: fileUrl={}", fileUrl, e);
            throw new IOException("从URL下载文件失败: " + e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭输入流失败", e);
                }
            }
        }
    }

    /**
     * 从InputStream读取Excel数据(核心方法)
     */
    private List<Map<String, Object>> readExcelDataFromInputStream(InputStream inputStream,
                                                                   Integer processId, Integer deptId)
            throws IOException {
        // 获取流程字段列表
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(processId);
        policyRequestVO.setDeptId(deptId);
        List<PolicyRequestDTO> fieldList = processPolicyService.getRequestData(policyRequestVO);

        if (fieldList == null || fieldList.isEmpty()) {
            throw new RuntimeException("流程入参字段配置为空");
        }

        // 同步读取Excel(跳过前2行表头)
        List<Map<Integer, String>> rawDataList = EasyExcel
                .read(inputStream)
                .sheet()
                .headRowNumber(2) // 跳过前2行表头
                .doReadSync();

        if (rawDataList == null || rawDataList.isEmpty()) {
            throw new RuntimeException("Excel文件中没有数据");
        }

        if (rawDataList.size() > MAX_ROW_COUNT) {
            throw new RuntimeException("数据行数不能超过" + MAX_ROW_COUNT + "行");
        }

        // 转换为Map<String, Object>
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<Integer, String> rowData : rawDataList) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 0; i < fieldList.size(); i++) {
                String fieldName = fieldList.get(i).getName();
                String value = rowData.get(i);
                row.put(fieldName, value);
            }
            resultList.add(row);
        }

        return resultList;
    }

    /**
     * 数据校验
     */
    private ValidateResult validateBatchData(List<Map<String, Object>> dataList,
                                             Integer processId, Integer deptId) {
        if (dataList == null || dataList.isEmpty()) {
            return ValidateResult.fail("没有可处理的数据");
        }

        // 获取流程字段配置
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(processId);
        policyRequestVO.setDeptId(deptId);
        List<PolicyRequestDTO> fieldList = processPolicyService.getRequestData(policyRequestVO);

        // 获取必填字段
        List<PolicyRequestDTO> requiredFields = fieldList.stream()
                .filter(field -> field.getIsRequired() != null && field.getIsRequired())
                .collect(Collectors.toList());

        // 校验每一行
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> rowData = dataList.get(i);

            // 校验必填字段
            for (PolicyRequestDTO field : requiredFields) {
                Object value = rowData.get(field.getName());
                if (value == null || StrUtil.isBlank(String.valueOf(value))) {
                    return ValidateResult.fail(
                            String.format("第%d行缺少必填字段: %s", i + 3, field.getNameZh()));
                }
            }
        }

        return ValidateResult.success();
    }

    /**
     * 创建批次记录
     *
     * @param fileUrl 文件URL(可选)
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelTaskRecordBatch createBatch(String batchNo, Integer processId, Integer responseForm,
                                            Integer userId, String userName, Integer deptId,
                                            String fileName, String fileUrl, int totalCount) {
        // 获取流程策略信息
        ProcessPolicy processPolicy = processPolicyService.getById(processId);

        ModelTaskRecordBatch batch = new ModelTaskRecordBatch();
        batch.setBatchNo(batchNo);
        batch.setProcessId(processId);
        batch.setProcessStrategy(processPolicy != null ? processPolicy.getProcessStrategy() : null);
        batch.setFileName(fileName);
        batch.setFileUrl(fileUrl); // 设置文件URL
        batch.setTotalCount(totalCount);
        batch.setSuccessCount(0);
        batch.setFailCount(0);
        batch.setBatchStatus(1); // 待处理
        batch.setUserId(userId);
        batch.setApplicationUser(userName);
        batch.setDeptId(deptId);
        batch.setResponseForm(responseForm);
        // 设置businessCode和modelName (从流程策略获取)
        if (processPolicy != null) {
            batch.setBusinessCode(processPolicy.getBusinessCode() != null ?
                    Long.parseLong(processPolicy.getBusinessCode()) : null);
            batch.setModelName(processPolicy.getProcessStrategy()); // 模型名称使用流程策略名称
        }
        batch.setDataStatus(0);
        batch.setCreateTime(LocalDateTime.now());

        batchMapper.insert(batch);
        return batch;
    }

    /**
     * 创建校验失败的批次
     */
    private Map<String, Object> createFailedBatch(Integer processId, Integer responseForm,
                                                  Integer userId, String userName, Integer deptId,
                                                  String fileName, String errorMessage) {
        String batchNo = "MBatch_" + SnowFlakeCloud.nextId();

        ModelTaskRecordBatch batch = new ModelTaskRecordBatch();
        batch.setBatchNo(batchNo);
        batch.setProcessId(processId);
        batch.setFileName(fileName);
        batch.setBatchStatus(6); // 校验失败
        batch.setErrorMessage(errorMessage);
        batch.setUserId(userId);
        batch.setApplicationUser(userName);
        batch.setDeptId(deptId);
        batch.setResponseForm(responseForm);
        batch.setDataStatus(0);
        batch.setCreateTime(LocalDateTime.now());

        batchMapper.insert(batch);

        Map<String, Object> result = new HashMap<>();
        result.put("batchNo", batchNo);
        result.put("batchId", batch.getId());
        result.put("error", errorMessage);
        return result;
    }

    /**
     * 构建任务VO
     */
    private ModelTaskRecordVO buildTaskVO(String taskNo, Map<String, Object> rowData,
                                         Integer processId, Integer responseForm,
                                         Integer userId, Integer deptId) {
        // 获取流程字段配置
        PolicyRequestVO policyRequestVO = new PolicyRequestVO();
        policyRequestVO.setProcessStrategyId(processId);
        policyRequestVO.setDeptId(deptId);
        List<PolicyRequestDTO> fieldList = processPolicyService.getRequestData(policyRequestVO);

        // 构建PolicyRequestList(与单笔执行格式保持一致)
        List<PolicyRequestDTO> policyRequestList = new ArrayList<>();
        for (PolicyRequestDTO field : fieldList) {
            PolicyRequestDTO requestDTO = new PolicyRequestDTO();
            requestDTO.setName(field.getName());
            requestDTO.setNameZh(field.getNameZh());
            requestDTO.setType(field.getType());
            requestDTO.setTypeName(field.getTypeName());
            requestDTO.setIsRequired(field.getIsRequired());
            // 从rowData中获取对应字段的值
            Object value = rowData.get(field.getName());
            requestDTO.setValue(value != null ? value.toString() : null);
            policyRequestList.add(requestDTO);
        }

        ModelTaskRecordVO taskVO = new ModelTaskRecordVO();
        taskVO.setTaskNo(taskNo);
        taskVO.setProcessId(processId);
        taskVO.setResponseForm(responseForm);
        taskVO.setProcessEntry(rowData); // 用于执行流程
        taskVO.setPolicyRequestList(policyRequestList); // 用于存储到数据库
        taskVO.setUserId(userId);
        taskVO.setDeptId(deptId);

        // 用户身份信息(复用现有逻辑需要)
        Map<String, Object> userIdentity = new HashMap<>();
        userIdentity.put("userId", userId);
        userIdentity.put("authorizationToken", deptId);
        taskVO.setUserIdentity(userIdentity);

        return taskVO;
    }

    /**
     * 创建任务记录
     *
     * @param taskVO 任务VO
     * @param batch 批次信息
     * @param processNodeList 流程节点列表
     * @param modelNameSnapshot 模型名称快照
     * @return 任务记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelTaskRecord createTaskRecord(ModelTaskRecordVO taskVO, ModelTaskRecordBatch batch,
                                           List<ProcessNode> processNodeList, String modelNameSnapshot) {
        ModelTaskRecord taskRecord = new ModelTaskRecord();
        taskRecord.setBatchId(batch.getId());
        taskRecord.setTaskNo(taskVO.getTaskNo());
        // 使用policyRequestList保持与单笔执行一致的数组格式
        taskRecord.setProcessEntry(JSON.toJSONString(taskVO.getPolicyRequestList()));
        taskRecord.setProcessId(taskVO.getProcessId());
        taskRecord.setResponseForm(taskVO.getResponseForm());
        taskRecord.setTaskStatus(2); // 生成中
        taskRecord.setDeptId(taskVO.getDeptId());
        // 从batch和taskVO复制字段
        taskRecord.setApplicationUser(taskVO.getApplicationUser()); // 从taskVO获取(已在调用前设置)
        taskRecord.setBusinessCode(batch.getBusinessCode() != null ? batch.getBusinessCode().intValue() : null);
        taskRecord.setProcessStrategy(batch.getProcessStrategy());
        taskRecord.setModelName(modelNameSnapshot); // 使用真实的模型名称快照
        taskRecord.setDataStatus(0);
        taskRecord.setCreateTime(LocalDateTime.now());

        // 保存流程节点快照
        String processNodesSnapshot = JSON.toJSONString(processNodeList);
        taskRecord.setProcessNodesSnapshot(processNodesSnapshot);

        taskRecordMapper.insert(taskRecord);
        return taskRecord;
    }

    /**
     * 获取模型名称快照
     * (与ModelTaskRecordServiceImpl中的方法保持一致)
     */
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
}
