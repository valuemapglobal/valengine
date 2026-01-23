package com.value.decision.model.decisionmanage.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模型任务记录批次表
 *
 * @author Claude
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_task_record_batch")
public class ModelTaskRecordBatch extends Model<ModelTaskRecordBatch> {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号(唯一)
     */
    private String batchNo;

    /**
     * 流程策略ID
     */
    private Integer processId;

    /**
     * 流程策略名称
     */
    private String processStrategy;

    /**
     * 上传的文件名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String fileUrl;

    /**
     * 总任务数
     */
    private Integer totalCount;

    /**
     * 成功任务数
     */
    private Integer successCount;

    /**
     * 失败任务数
     */
    private Integer failCount;

    /**
     * 批次状态: 1待处理,2处理中,3全部成功,4部分失败,5全部失败,6校验失败
     */
    private Integer batchStatus;

    /**
     * 错误信息(校验失败时记录)
     */
    private String errorMessage;

    /**
     * 创建用户ID
     */
    private Integer userId;

    /**
     * 创建用户名
     */
    private String applicationUser;

    /**
     * 部门ID
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 业务场景
     */
    private Long businessCode;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型名称列表(从modelName按逗号分割得到,不存储到数据库)
     */
    @TableField(exist = false)
    private List<String> modelNameList;

    /**
     * 响应形式 1数据 2报告
     */
    private Integer responseForm;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 开始处理时间
     */
    private LocalDateTime startTime;

    /**
     * 结束处理时间
     */
    private LocalDateTime finishTime;

    /**
     * 数据状态: 0正常,1删除
     */
    private Integer dataStatus;
}
