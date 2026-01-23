package com.value.decision.model.decisionmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.ModelTaskRecord;
import com.value.decision.model.decisionmanage.model.dto.EvaluationReportDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelRuleResultDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTaskRecordVO;
import com.value.decision.model.decisionmanage.model.dto.model.TaskRecordDTO;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

/**
 * 模型任务记录表 服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface IModelTaskRecordService extends IService<ModelTaskRecord> {

    //流程任务启动
    void processTaskInitiation(ModelTaskRecordVO modelTaskRecordVO) throws UnsupportedEncodingException;

    //流程任务启动（同步）
    Object processTaskInitiationSync(ModelTaskRecordVO modelTaskRecordVO) throws Exception;

    //批次任务执行(不创建任务记录,仅执行流程)
    void executeProcessFlowForBatch(ModelTaskRecordVO modelTaskRecordVO) throws UnsupportedEncodingException;

    //流程任务列表
    List<TaskRecordDTO> taskRecordList(ModelTaskRecordVO modelTaskRecordVO) throws ParseException;

    //数据响应形式
    ModelRuleResultDTO dataResponseForm(ModelTaskRecordVO modelTaskRecordVO);

    //报告响应形式
    EvaluationReportDTO getReportResponseForm(ModelTaskRecordVO modelTaskRecordVO);
}