package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.RateCardRecord;
import com.value.decision.model.decisionmanage.service.IRateCardRecordService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评级卡主表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@RestController
@RequestMapping("/rate-card-record")
public class RateCardRecordController {

    @Autowired
    private IRateCardRecordService rateCardRecordService;

    @Log(title = "评级更新状态", businessType = BusinessType.UPDATE)
    @RequestMapping("updateStatus")
    public AjaxResult updateStatus(@RequestBody RateCardRecord rateCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRecordService.updateStatus(rateCardRecord);
    }

    @RequestMapping("list")
    public AjaxResult list(@RequestBody RateCardRecord rateCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRecordService.newList(rateCardRecord,loginUser);
    }

    @RequestMapping("release")
    public AjaxResult release(@RequestBody RateCardRecord rateCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRecordService.release(rateCardRecord,loginUser);
    }

}
