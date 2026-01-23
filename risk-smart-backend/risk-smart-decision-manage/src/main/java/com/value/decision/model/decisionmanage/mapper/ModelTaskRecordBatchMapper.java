package com.value.decision.model.decisionmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.decision.model.decisionmanage.model.ModelTaskRecordBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 模型任务记录批次表 Mapper接口
 *
 * @author Claude
 * @since 2025-01-14
 */
@Mapper
public interface ModelTaskRecordBatchMapper extends BaseMapper<ModelTaskRecordBatch> {

    /**
     * 统计批次下指定状态的任务数
     *
     * @param batchId 批次ID
     * @param status 任务状态
     * @return 任务数量
     */
    int countTasksByStatus(@Param("batchId") Long batchId, @Param("status") Integer status);
}
