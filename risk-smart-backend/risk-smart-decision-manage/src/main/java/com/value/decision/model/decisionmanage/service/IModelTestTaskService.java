package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.ModelTestTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskVO;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 模型测试任务表 服务类
 * </p>
 *
 * @author hc
 * @since 2024-11-06
 */
public interface IModelTestTaskService extends IService<ModelTestTask> {

    void modelTestRecord(ModelTestTaskVO modelTestTaskVO) throws UnsupportedEncodingException;

    List<ModelTestTaskDTO> modelTestTaskList(ModelTestTaskVO modelTestTaskVO) throws ParseException;

}
