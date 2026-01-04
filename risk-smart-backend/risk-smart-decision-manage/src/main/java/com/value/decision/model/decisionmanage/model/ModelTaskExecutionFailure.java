package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 任务执行失败日志实体
 *
 * @author Austin
 * @since 2025-01-16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("model_task_execution_failure")
public class ModelTaskExecutionFailure extends Model<ModelTaskExecutionFailure> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的任务号 (model_task_record.task_no)
     */
    private String taskNo;

    /**
     * 关联的批次ID (批量任务时非空)
     */
    private Long batchId;

    /**
     * 关联的流程策略ID
     */
    private Integer processId;

    /**
     * 失败发生时间
     */
    private LocalDateTime failureTime;

    /**
     * 场景编号 (例如: A1, E3)
     */
    private String sceneCode;

    /**
     * 失败大类 (外部服务异常/提交信息有误/系统内部异常)
     */
    private String failureCategory;

    /**
     * 具体失败场景 (数据中台接口超时)
     */
    private String failureScene;

    /**
     * 是否可重试 (1=是, 0=否)
     */
    private Integer isRetryable;

    /**
     * 给用户的友好提示
     */
    private String userMessage;

    /**
     * JSON格式的技术细节 (异常消息、关键参数、外部接口响应等)
     */
    private String technicalDetail;

    /**
     * 异常堆栈摘要
     */
    private String stackTraceSnippet;

    /**
     * 数据状态 (0=正常, 1=已删除)
     */
    private Integer dataStatus;
}
