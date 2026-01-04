package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.enums.FailureSceneEnum;
import com.value.decision.model.decisionmanage.model.ModelTaskExecutionFailure;
import com.value.decision.model.decisionmanage.model.dto.TaskFailureDetailDTO;

import java.util.Map;

/**
 * 失败日志记录服务接口
 *
 * @author Austin
 * @since 2025-01-16
 */
public interface IFailureLogService extends IService<ModelTaskExecutionFailure> {

    /**
     * 异步记录失败日志(推荐使用,不阻塞主流程)
     *
     * @param taskNo    任务编号
     * @param scene     失败场景枚举
     * @param exception 原始异常
     * @param context   额外上下文信息(可选)
     */
    void logFailureAsync(String taskNo,
                         FailureSceneEnum scene,
                         Exception exception,
                         Map<String, Object> context);

    /**
     * 同步记录失败日志(仅在关键场景使用,确保记录成功)
     *
     * @param taskNo    任务编号
     * @param scene     失败场景枚举
     * @param exception 异常对象
     */
    void logFailureSync(String taskNo, FailureSceneEnum scene, Exception exception);

    /**
     * 获取任务失败详情
     *
     * @param taskNo 任务编号
     * @param deptId 部门ID(用于权限校验)
     * @return 失败详情DTO,任务不存在或无权限时返回null
     */
    TaskFailureDetailDTO getFailureDetail(String taskNo, Integer deptId);
}
