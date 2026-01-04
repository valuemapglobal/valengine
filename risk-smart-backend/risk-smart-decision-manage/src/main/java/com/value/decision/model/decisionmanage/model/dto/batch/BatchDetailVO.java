package com.value.decision.model.decisionmanage.model.dto.batch;

import com.value.decision.model.decisionmanage.model.ModelTaskRecord;
import com.value.decision.model.decisionmanage.model.ModelTaskRecordBatch;
import lombok.Data;

import java.util.List;

/**
 * 批次详情VO
 *
 * @author Claude
 * @since 2025-01-14
 */
@Data
public class BatchDetailVO {

    /**
     * 批次信息
     */
    private ModelTaskRecordBatch batch;

    /**
     * 任务列表
     */
    private List<ModelTaskRecord> taskList;

    /**
     * 总记录数
     */
    private Long total;
}
