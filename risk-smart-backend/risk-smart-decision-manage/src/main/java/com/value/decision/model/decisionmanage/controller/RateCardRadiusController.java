package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.RateCardRadius;
import com.value.decision.model.decisionmanage.service.IRateCardRadiusService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评级范围表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@RestController
@RequestMapping("/rate-card-radius")
public class RateCardRadiusController {

    @Autowired
    private IRateCardRadiusService rateCardRadiusService;

    @Log(title = "评级规则新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    public AjaxResult submit(@Validated @RequestBody RateCardRadius rateCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRadiusService.submit(rateCardRadius,SecurityUtils.getLoginUser());
    }

    @Log(title = "评级规则编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    public AjaxResult update(@Validated @RequestBody RateCardRadius rateCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRadiusService.submit(rateCardRadius,SecurityUtils.getLoginUser());
    }

    @RequestMapping("get")
    public AjaxResult get(@RequestBody RateCardRadius rateCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return rateCardRadiusService.get(rateCardRadius,SecurityUtils.getLoginUser());
    }



}
