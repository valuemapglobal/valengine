package com.value.decision.model.decisionmanage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.enums.FailureSceneEnum;
import com.value.decision.model.decisionmanage.mapper.ModelTaskExecutionFailureMapper;
import com.value.decision.model.decisionmanage.mapper.ModelTaskRecordMapper;
import com.value.decision.model.decisionmanage.model.ModelTaskExecutionFailure;
import com.value.decision.model.decisionmanage.model.ModelTaskRecord;
import com.value.decision.model.decisionmanage.model.dto.TaskFailureDetailDTO;
import com.value.decision.model.decisionmanage.service.IFailureLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 失败日志记录服务实现类
 *
 * @author Austin
 * @since 2025-01-16
 */
@Slf4j
@Service
public class FailureLogServiceImpl extends ServiceImpl<ModelTaskExecutionFailureMapper, ModelTaskExecutionFailure>
        implements IFailureLogService {

    @Autowired
    private ModelTaskRecordMapper modelTaskRecordMapper;

    /**
     * 异步记录失败日志
     *
     * @param taskNo    任务编号
     * @param scene     失败场景枚举
     * @param exception 原始异常
     * @param context   额外上下文信息(可选)
     */
    @Override
    @Async("failureLogExecutor")
    public void logFailureAsync(String taskNo,
                                FailureSceneEnum scene,
                                Exception exception,
                                Map<String, Object> context) {
        try {
            ModelTaskExecutionFailure logEntity = buildFailureLog(taskNo, scene, exception, context);
            this.save(logEntity);
            log.info("失败日志记录成功: taskNo={}, sceneCode={}", taskNo, scene.getSceneCode());
        } catch (Exception e) {
            log.error("保存失败日志时出错: taskNo={}", taskNo, e);
        }
    }

    /**
     * 同步记录失败日志(仅在关键场景使用,确保记录成功)
     *
     * @param taskNo    任务编号
     * @param scene     失败场景枚举
     * @param exception 异常对象
     */
    @Override
    public void logFailureSync(String taskNo, FailureSceneEnum scene, Exception exception) {
        ModelTaskExecutionFailure logEntity = buildFailureLog(taskNo, scene, exception, null);
        this.save(logEntity);
    }

    /**
     * 构建失败日志对象
     *
     * @param taskNo    任务编号
     * @param scene     失败场景枚举
     * @param exception 异常对象
     * @param context   上下文信息
     * @return 失败日志对象
     */
    private ModelTaskExecutionFailure buildFailureLog(String taskNo,
                                                      FailureSceneEnum scene,
                                                      Exception exception,
                                                      Map<String, Object> context) {
        ModelTaskExecutionFailure logEntity = new ModelTaskExecutionFailure();

        // 基础信息
        logEntity.setTaskNo(taskNo);
        logEntity.setFailureTime(LocalDateTime.now());
        logEntity.setDataStatus(0);  // 正常状态

        // 分类信息(直接从枚举获取)
        logEntity.setSceneCode(scene.getSceneCode());
        logEntity.setFailureCategory(scene.getFailureCategory());
        logEntity.setFailureScene(scene.getFailureScene());
        logEntity.setIsRetryable(scene.isRetryable() ? 1 : 0);

        // 用户消息(优先从数据中台提取，其次使用枚举默认值)
        String userMessage = null;
        
        // 尝试从异常消息中提取数据中台的msg字段
        if (exception != null && exception.getMessage() != null) {
            String exceptionMsg = exception.getMessage();
            if (exceptionMsg.contains("数据中台") && exceptionMsg.contains("返还参数")) {
                String dataPlatformMsg = FailureSceneEnum.extractDataPlatformMessage(exceptionMsg);
                if (dataPlatformMsg != null && !dataPlatformMsg.trim().isEmpty()) {
                    userMessage = dataPlatformMsg;
                    log.info("从数据中台返回参数中提取到用户消息: {}", userMessage);
                }
            }
        }
        
        // 如果未从数据中台提取到，使用枚举默认值
        if (userMessage == null || userMessage.trim().isEmpty()) {
            userMessage = scene.getUserMessage();
        }

        // 兜底逻辑：如果userMessage为空，使用异常消息或默认值
        if (userMessage == null || userMessage.trim().isEmpty()) {
            if (exception != null && exception.getMessage() != null && !exception.getMessage().trim().isEmpty()) {
                // 使用异常消息作为用户消息
                userMessage = exception.getMessage();
                // 限制长度，避免过长
                if (userMessage.length() > 500) {
                    userMessage = userMessage.substring(0, 500);
                }
            } else {
                // 如果异常消息也为空，使用默认值
                userMessage = "系统处理异常，请联系技术支持";
            }
            log.warn("场景 {} 的userMessage为空，使用兜底值: {}", scene.getSceneCode(), userMessage);
        }

        logEntity.setUserMessage(userMessage);

        // 技术详情(构建JSON)
        JSONObject technicalDetail = new JSONObject();
        if (exception != null) {
            technicalDetail.put("exceptionClass", exception.getClass().getName());
            technicalDetail.put("exceptionMessage", exception.getMessage());

            // 堆栈摘要
            String stackTrace = ExceptionUtils.getStackTrace(exception);
            if (stackTrace != null) {
                // 限制堆栈长度,存储前3000字符
                if (stackTrace.length() > 3000) {
                    logEntity.setStackTraceSnippet(stackTrace.substring(0, 3000) + "\n...[truncated]");
                } else {
                    logEntity.setStackTraceSnippet(stackTrace);
                }
            }
        }

        // 添加上下文信息到technical_detail
        if (context != null && !context.isEmpty()) {
            // 提取批次ID和流程ID
            if (context.containsKey("batchId")) {
                logEntity.setBatchId((Long) context.get("batchId"));
            }
            if (context.containsKey("processId")) {
                logEntity.setProcessId((Integer) context.get("processId"));
            }

            // 其他上下文信息存入JSON
            context.forEach((key, value) -> {
                if (!"batchId".equals(key) && !"processId".equals(key)) {
                    try {
                        // 限制单个字段长度,避免JSON过大
                        String valueStr = value != null ? value.toString() : "";
                        if (valueStr.length() > 500) {
                            valueStr = valueStr.substring(0, 500) + "...[truncated]";
                        }
                        technicalDetail.put(key, valueStr);
                    } catch (Exception e) {
                        log.warn("添加上下文信息失败: key={}", key, e);
                    }
                }
            });
        }

        // 设置技术详情JSON
        String technicalDetailJson = technicalDetail.toJSONString();
        // TEXT类型可以存储较长内容,但仍建议限制在合理范围
        if (technicalDetailJson.length() > 10000) {
            technicalDetailJson = technicalDetailJson.substring(0, 10000) + "...[truncated]";
        }
        logEntity.setTechnicalDetail(technicalDetailJson);

        return logEntity;
    }

    /**
     * 获取任务失败详情
     *
     * @param taskNo 任务编号
     * @param deptId 部门ID(用于权限校验)
     * @return 失败详情DTO,任务不存在或无权限时返回null
     */
    @Override
    public TaskFailureDetailDTO getFailureDetail(String taskNo, Integer deptId) {
        // 1. 查询任务记录(用于权限校验)
        LambdaQueryWrapper<ModelTaskRecord> taskWrapper = Wrappers.lambdaQuery();
        taskWrapper.eq(ModelTaskRecord::getTaskNo, taskNo)
                   .eq(ModelTaskRecord::getDataStatus, 0);
        ModelTaskRecord taskRecord = modelTaskRecordMapper.selectOne(taskWrapper);

        // 2. 任务不存在
        if (taskRecord == null) {
            log.warn("任务不存在: taskNo={}", taskNo);
            return null;
        }

        // 3. 权限校验(如果提供了deptId)
        if (deptId != null && !deptId.equals(taskRecord.getDeptId())) {
            log.warn("无权限查看任务失败详情: taskNo={}, requestDeptId={}, taskDeptId={}",
                    taskNo, deptId, taskRecord.getDeptId());
            return null;
        }

        // 4. 查询失败日志(取最新一条)
        LambdaQueryWrapper<ModelTaskExecutionFailure> failureWrapper = Wrappers.lambdaQuery();
        failureWrapper.eq(ModelTaskExecutionFailure::getTaskNo, taskNo)
                      .eq(ModelTaskExecutionFailure::getDataStatus, 0)
                      .orderByDesc(ModelTaskExecutionFailure::getFailureTime)
                      .last("LIMIT 1");
        ModelTaskExecutionFailure failureLog = this.getOne(failureWrapper);

        // 5. 失败日志不存在
        if (failureLog == null) {
            log.warn("失败日志不存在: taskNo={}", taskNo);
            return null;
        }

        // 6. 构建返回DTO
        TaskFailureDetailDTO detailDTO = new TaskFailureDetailDTO();
        detailDTO.setTaskNo(taskNo);
        detailDTO.setTaskStatus(taskRecord.getTaskStatus());
        detailDTO.setProcessId(taskRecord.getProcessId());
        detailDTO.setProcessStrategy(taskRecord.getProcessStrategy());
        detailDTO.setFailureTime(failureLog.getFailureTime());
        detailDTO.setSceneCode(failureLog.getSceneCode());
        detailDTO.setFailureCategory(failureLog.getFailureCategory());
        detailDTO.setFailureScene(failureLog.getFailureScene());
        detailDTO.setUserMessage(failureLog.getUserMessage());
        detailDTO.setIsRetryable(failureLog.getIsRetryable());

        // 7. 生成重试建议
        String retryTips = generateRetryTips(failureLog.getSceneCode(), failureLog.getIsRetryable());
        detailDTO.setRetryTips(retryTips);

        // 8. 解析技术详情JSON
        if (failureLog.getTechnicalDetail() != null) {
            try {
                Map<String, Object> technicalDetail = JSON.parseObject(
                        failureLog.getTechnicalDetail(),
                        Map.class
                );
                detailDTO.setTechnicalDetail(technicalDetail);
            } catch (Exception e) {
                log.warn("解析技术详情JSON失败: taskNo={}", taskNo, e);
                // 解析失败时设置为空Map
                detailDTO.setTechnicalDetail(new HashMap<>());
            }
        }

        return detailDTO;
    }

    /**
     * 生成重试建议
     *
     * @param sceneCode   场景编码
     * @param isRetryable 是否可重试
     * @return 重试建议文本
     */
    private String generateRetryTips(String sceneCode, Integer isRetryable) {
        if (isRetryable == null || isRetryable == 0) {
            // 不可重试的场景,给出修复建议
            switch (sceneCode) {
                case "A3": // 数据中台返回数据为空
                    return "请检查企业统一社会信用代码是否正确,确认企业是否在数据库中存在";
                case "E1": // 订单号重复
                    return "请检查是否已经提交过该订单,避免重复提交";
                case "E2": // 必填参数缺失
                    return "请检查提交的数据是否完整,补充缺失的必填字段";
                case "E3": // 参数格式错误
                    return "请检查提交的数据格式是否正确,如日期、金额等字段";
                case "B1": // 流程策略未配置节点
                case "B2": // 流程策略不存在
                case "B3": // 模型配置缺失
                case "B4": // 流程节点模型类型无效
                case "C1": // 模型规则配置为空
                case "C2": // 模型数据源配置为空
                case "C3": // 模型配置格式错误
                    return "请联系系统管理员检查流程和模型配置";
                default:
                    return "请检查提交的数据或联系技术支持";
            }
        } else {
            // 可重试的场景,建议重试
            switch (sceneCode) {
                case "A1": // 数据中台接口超时
                    return "外部服务暂时不可用,请稍等片刻后重新提交";
                case "A4": // 数据中台返回JSON格式异常
                case "A7": // 决策引擎返回格式异常
                    return "外部服务返回数据异常,请稍后重试或联系技术支持";
                case "A5": // Feign服务调用失败
                case "A6": // 决策引擎服务不可用
                    return "外部服务暂时不可用,请稍后重试";
                case "F1": // 数据库连接异常
                case "F2": // Redis连接异常
                    return "系统服务暂时不可用,请稍后重试";
                case "F4": // 系统资源耗尽
                    return "系统负载过高,请稍等片刻后重新提交";
                default:
                    return "请稍后重试,如问题持续请联系技术支持";
            }
        }
    }
}
