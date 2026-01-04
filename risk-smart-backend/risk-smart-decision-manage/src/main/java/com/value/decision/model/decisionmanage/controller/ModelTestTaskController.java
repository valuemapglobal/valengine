package com.value.decision.model.decisionmanage.controller;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.decision.common.constant.SecurityConstants;
import com.value.decision.common.utils.CommonUtil;
import com.value.decision.common.utils.SnowFlakeCloud;
import com.value.decision.common.utils.StringUtils;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.ModelTestTask;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelTestTaskVO;
import com.value.decision.model.decisionmanage.service.IModelTestTaskService;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 模型测试任务表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2024-11-06
 */
@RestController
@RequestMapping("/model-test-task")
public class ModelTestTaskController {

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @Autowired
    private IModelTestTaskService iModelTestTaskService;

    /**
     * 数据初始化测试
     */
    @PostMapping("/testData")
    public AjaxResult testData(@RequestBody List<String> manageList){
        JSONObject enteredGinsengByManageNo = commonRuleFunctionDataNew.getEnteredGinsengByManageNo(manageList);
        return AjaxResult.success(enteredGinsengByManageNo);
    }

    /**
     * 模型测试任务启动
     * @param modelTestTaskVO
     * @return
     */
    @RequirLoginUser
    @PostMapping("/modelTest")
    public AjaxResult modelTest(@RequestBody ModelTestTaskVO modelTestTaskVO) throws UnsupportedEncodingException {

        //雪花算法
        String taskNo = String.valueOf("MCS"+ SnowFlakeCloud.nextId());
        modelTestTaskVO.setTaskNo(taskNo);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Thread thread = new Thread(() -> {
            try {
                iModelTestTaskService.modelTestRecord(modelTestTaskVO);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //异常推送飞书消息
                CommonUtil.sendBotMessage("模型测试接口异常，请联系管理员！");
                return;
            }
        });
        executorService.execute(thread);
        executorService.shutdown();

        Map<String,Object> map = new HashMap<>();
        map.put("taskNo",taskNo);
        return AjaxResult.success(map);
    }


    /**
     * 测试任务列表查询
     * @param modelTestTaskVO
     * @return
     */
    @PostMapping("/selectList")
    public AjaxResult selectList(@RequestBody ModelTestTaskVO modelTestTaskVO) throws ParseException {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        modelTestTaskVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        List<ModelTestTaskDTO> modelTestTaskDTOList = iModelTestTaskService.modelTestTaskList(modelTestTaskVO);
        AjaxResult paging = commonRuleFunctionDataNew.paging(modelTestTaskDTOList, modelTestTaskVO.getPageNum(), modelTestTaskVO.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    /**
     * 任务号查询运行drl文件
     * @param taskNo
     * @return
     */
    @GetMapping("/selectDrl")
    public AjaxResult selectDrl(@RequestParam(required = false) String taskNo){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        if (StringUtils.isEmpty(taskNo)){
               return AjaxResult.error("查询任务号不能为空");
        }
        LambdaQueryWrapper<ModelTestTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelTestTask::getTaskNo,taskNo)
                .eq(ModelTestTask::getDataStatus,0);
        List<ModelTestTask> list = iModelTestTaskService.list(wrapper);
        Map<String,Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(list.get(0).getTestDrl())){
            map.put("testDrl",list.get(0).getTestDrl());
        }else {
            map.put("testDrl",new ArrayList<>());
        }
        return AjaxResult.success(map);
    }


}
