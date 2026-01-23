package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.ModelTaskRecordBatch;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchDetailVO;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchQueryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 模型任务记录批次 Service接口
 *
 * @author Claude
 * @since 2025-01-14
 */
public interface IModelTaskRecordBatchService extends IService<ModelTaskRecordBatch> {

    /**
     * 上传文件并校验(不执行任务,仅校验并返回文件信息)
     *
     * @param file 上传的Excel文件
     * @param processId 流程策略ID
     * @param deptId 部门ID
     * @return 包含fileUrl、数据行数等信息
     */
    Map<String, Object> uploadFile(MultipartFile file, Integer processId, Integer deptId);

    /**
     * 提交批次任务(创建批次并异步执行)
     *
     * @param fileUrl 文件URL
     * @param fileName 文件名
     * @param processId 流程策略ID
     * @param responseForm 响应形式 1数据 2报告
     * @param userId 用户ID
     * @param userName 用户名
     * @param deptId 部门ID
     * @return 批次信息
     */
    Map<String, Object> submitBatch(String fileUrl, String fileName, Integer processId,
                                   Integer responseForm, Integer userId,
                                   String userName, Integer deptId);


    /**
     * 异步处理批量任务
     *
     * @param batchId 批次ID
     * @param dataList 数据列表
     * @param taskNos 预先生成的任务号列表
     * @param processId 流程策略ID
     * @param responseForm 响应形式
     * @param userId 用户ID
     * @param deptId 部门ID
     */
    void processBatchAsync(Long batchId, List<Map<String, Object>> dataList, List<String> taskNos,
                          Integer processId, Integer responseForm,
                          Integer userId, Integer deptId);

    /**
     * 查询批次列表
     *
     * @param queryDTO 查询条件
     * @return 批次列表
     */
    List<ModelTaskRecordBatch> getBatchList(BatchQueryDTO queryDTO);

    /**
     * 查询批次详情
     *
     * @param batchId 批次ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 批次详情
     */
    BatchDetailVO getBatchDetail(Long batchId, Integer pageNum, Integer pageSize);
}
