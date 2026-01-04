package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.PriceCardRecord;
import com.value.decision.model.decisionmanage.service.IPriceCardRecordService;
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
 * 定价卡主表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/price-card-record")
public class PriceCardRecordController {

    @Autowired
    private IPriceCardRecordService priceCardRecordService;

    @RequestMapping("list")
    public AjaxResult list(@RequestBody PriceCardRecord priceCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return priceCardRecordService.newList(priceCardRecord,loginUser);
    }

    @Log(title = "定价更新状态", businessType = BusinessType.UPDATE)
    @RequestMapping("updateStatus")
    public AjaxResult updateStatus(@RequestBody PriceCardRecord priceCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return priceCardRecordService.updateStatus(priceCardRecord);
    }

    @RequestMapping("release")
    public AjaxResult release(@RequestBody PriceCardRecord priceCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return priceCardRecordService.release(priceCardRecord,loginUser);
    }

}
