package com.value.decision.model.decisionmanage.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.util.StringUtil;
import com.value.decision.model.decisionmanage.mapper.*;
import com.value.decision.model.decisionmanage.model.ModelTestTask;
import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskDataVO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskVO;
import com.value.decision.model.decisionmanage.service.IModelTestTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeModelAntiFraudRuleRecordMapper;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 模型测试任务表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2024-11-06
 */
@Service
public class ModelTestTaskServiceImpl extends ServiceImpl<ModelTestTaskMapper, ModelTestTask> implements IModelTestTaskService {

    @Autowired
    private IModelTestTaskService iModelTestTaskService;

    @Autowired
    private RdeModelAntiFraudMapper rdeModelAntiFraudMapper;

    @Autowired
    private RdeModelAntiFraudRuleGroupMapper rdeModelAntiFraudRuleGroupMapper;

    @Autowired
    private RdeModelAntiFraudRuleRecordMapper rdeModelAntiFraudRuleRecordMapper;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @Autowired
    private ModelTestTaskMapper modelTestTaskMapper;

    @Autowired
    private ScoreCardRecordMapper scoreCardRecordMapper;

    @Autowired
    private ScoreCardReuseMapper scoreCardReuseMapper;

    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 模型测试
     * @param modelTestTaskVO
     */
    public void modelTestRecord(ModelTestTaskVO modelTestTaskVO) throws UnsupportedEncodingException {

        LoginUser loginUser = SecurityUtils.getLoginUser();

        ModelTestTaskDataVO modelTestTaskDataVO = new ModelTestTaskDataVO();
        modelTestTaskDataVO.setTaskNo(modelTestTaskVO.getTaskNo());
        modelTestTaskDataVO.setApplyUserName(loginUser.getSysUser().getUserId().intValue());
        modelTestTaskDataVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        modelTestTaskDataVO.setUserId(loginUser.getSysUser().getUserId().intValue());

        //根据模型id找到模型信息
        switch (modelTestTaskVO.getRuleCode()){
            case 1:
                LambdaQueryWrapper<ScoreCardRecord> cardRecordWrapper = Wrappers.lambdaQuery();
                cardRecordWrapper.eq(ScoreCardRecord::getId,modelTestTaskVO.getModelId())
                                 .eq(ScoreCardRecord::getButtonState,1)
                                 .eq(ScoreCardRecord::getDataState,0);
                ScoreCardRecord scoreCardRecord = scoreCardRecordMapper.selectOne(cardRecordWrapper);
                modelTestTaskDataVO.setProjectCode(Integer.parseInt(scoreCardRecord.getProjectCode()));
                modelTestTaskDataVO.setBusinessCode(Integer.parseInt(scoreCardRecord.getBusinessCode()));
                modelTestTaskDataVO.setRuleCode(Integer.parseInt(scoreCardRecord.getRuleCode()));
                modelTestTaskDataVO.setModelVerson(scoreCardRecord.getVersionControl());
                break;
            case 5:
            case 6:
                LambdaQueryWrapper<RdeModelAntiFraud> antiFraudWrapper = Wrappers.lambdaQuery();
                antiFraudWrapper.eq(RdeModelAntiFraud::getId,modelTestTaskVO.getModelId())
                        .eq(RdeModelAntiFraud::getStatus,1)
                        .eq(RdeModelAntiFraud::getDataStatus,0);
                RdeModelAntiFraud rdeModelAntiFraud = rdeModelAntiFraudMapper.selectRecord(modelTestTaskVO.getModelId());
                modelTestTaskDataVO.setProjectCode(Integer.parseInt(rdeModelAntiFraud.getProjectCode()));
                modelTestTaskDataVO.setBusinessCode(Integer.parseInt(rdeModelAntiFraud.getBusinessCode()));
                modelTestTaskDataVO.setRuleCode(Integer.parseInt(rdeModelAntiFraud.getRuleCode()));
                modelTestTaskDataVO.setModelVerson(rdeModelAntiFraud.getVersionControl());
                break;
        }

        //1,保存至模型任务表
        ModelTestTask modelTestTask = new ModelTestTask();
        modelTestTask.setTaskNo(modelTestTaskDataVO.getTaskNo());
        modelTestTask.setModelId(modelTestTaskVO.getModelId());
        modelTestTask.setModelVerson(modelTestTaskDataVO.getModelVerson());
        modelTestTask.setProjectCode(modelTestTaskDataVO.getProjectCode());
        modelTestTask.setBusinessCode(modelTestTaskDataVO.getBusinessCode());
        modelTestTask.setRuleCode(modelTestTaskDataVO.getRuleCode());
        modelTestTask.setDeptId(modelTestTaskDataVO.getDeptId());
        modelTestTask.setUserId(modelTestTaskDataVO.getUserId());
        modelTestTask.setTestStatus(1);
        iModelTestTaskService.save(modelTestTask);

        //2,根据模型id找到所有的规则进行组装,拿到data_module中所有manage_no 去重
        JSONObject enteredGinsengByManageNo = null; //json入参数据
        List<Map<String, Object>> requestData = commonRuleFunctionDataNew.getRequestData(modelTestTask);
        if (requestData != null){
            List<String> manageNoList = new ArrayList<>();
            requestData.forEach(map -> {
                manageNoList.add(map.get("manageNo").toString());
            });
            enteredGinsengByManageNo = commonRuleFunctionDataNew.getEnteredGinsengByManageNo(manageNoList);
        }
        modelTestTaskDataVO.setRuleData(enteredGinsengByManageNo);

        //3,获取drl脚本
        List<String> drlData = commonRuleFunctionDataNew.getDrlData(modelTestTask);

        //4,请求决策引擎判断模型配置是否正常
        commonRuleFunctionDataNew.getRuleCode(modelTestTaskDataVO,drlData,modelTestTaskVO);

    }

    @Override
    public List<ModelTestTaskDTO> modelTestTaskList(ModelTestTaskVO modelTestTaskVO) throws ParseException {

        LambdaQueryWrapper<ModelTestTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelTestTask::getModelId,modelTestTaskVO.getModelId())
                .eq(ModelTestTask::getDeptId,modelTestTaskVO.getDeptId())
                .eq(ModelTestTask::getProjectCode,modelTestTaskVO.getProductCode())
                .eq(ModelTestTask::getBusinessCode,modelTestTaskVO.getBusinessCode())
                .eq(ModelTestTask::getRuleCode,modelTestTaskVO.getRuleCode())
                .eq(ModelTestTask::getDataStatus,0);
        List<ModelTestTask> modelTestTaskList = modelTestTaskMapper.selectList(wrapper);
        List<ModelTestTaskDTO> modelTestTaskDTOList = new ArrayList<>();

        //查询模型名称 描述
//        RdeModelAntiFraud rdeModelAntiFraud = rdeModelAntiFraudMapper.selectById(modelTestTaskVO.getModelId());
        //业务场景标识
//        LambdaQueryWrapper<Business> businessWrapper = Wrappers.lambdaQuery();
//        businessWrapper.eq(Business::getId,modelTestTaskVO.getBusin/process-policy/selectessCode())
//                .eq(Business::getDataStatus,0);
//        Business business = businessMapper.selectOne(businessWrapper);
        List<ModelTestTaskDTO> finalModelTestTaskDTOList = modelTestTaskDTOList;
        modelTestTaskList.stream().forEach(y ->{
            ModelTestTaskDTO modelTestTaskDTO = new ModelTestTaskDTO();
            modelTestTaskDTO.setId(finalModelTestTaskDTOList.size()+1);
            modelTestTaskDTO.setTaskNo(y.getTaskNo());
            modelTestTaskDTO.setCreateTime(y.getCreateTime());
            modelTestTaskDTO.setModelName(modelTestTaskVO.getModelName());
            modelTestTaskDTO.setModelRemark(modelTestTaskVO.getModelRemark() == null?"-":modelTestTaskVO.getModelRemark());
            modelTestTaskDTO.setModelVerson(y.getModelVerson());
            modelTestTaskDTO.setTestStatus(y.getTestStatus());
            modelTestTaskDTO.setBusinessName(modelTestTaskVO.getBusinessName());
            modelTestTaskDTO.setRuleName(ruleName(y.getRuleCode()));
            finalModelTestTaskDTOList.add(modelTestTaskDTO);
        });

        if (CollectionUtils.isNotEmpty(modelTestTaskDTOList)){
            modelTestTaskDTOList = finalModelTestTaskDTOList.stream().sorted(Comparator.comparing(ModelTestTaskDTO::getCreateTime).reversed()).collect(Collectors.toList());
        }

        // 定义时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将字符串转换为Date对象
        Date startTime = modelTestTaskVO.getStartTime() != null?dateFormat.parse(modelTestTaskVO.getStartTime()):null;
        Date endTime = modelTestTaskVO.getEndTime() != null?dateFormat.parse(modelTestTaskVO.getEndTime()):null;

        List<ModelTestTaskDTO> collect = modelTestTaskDTOList.stream().filter(x -> (StringUtil.isEmpty(modelTestTaskVO.getTaskNo()) || x.getTaskNo().contains(modelTestTaskVO.getTaskNo()))
                    && (modelTestTaskVO.getTestStatus() == null || modelTestTaskVO.getTestStatus() == x.getTestStatus())
                    && ((modelTestTaskVO.getStartTime() == null && modelTestTaskVO.getEndTime() == null) || (!x.getCreateTime().before(startTime)) && !x.getCreateTime().after(endTime))).collect(Collectors.toList());
        if (StringUtil.isNotEmpty(modelTestTaskVO.getTaskNo()) || modelTestTaskVO.getTestStatus() != null || (modelTestTaskVO.getStartTime() != null && modelTestTaskVO.getEndTime() != null)){
            return collect;
        }
        return modelTestTaskDTOList;
    }

    /**
     * 策略场景标识
     */
    private static String ruleName(Integer ruleCode){

        switch (ruleCode){
            case 1:
                return "评分模型";
            case 5:
                return "规则模型";
            case 6:
                return "分类模型";
        }
        return "-";
    }
}
