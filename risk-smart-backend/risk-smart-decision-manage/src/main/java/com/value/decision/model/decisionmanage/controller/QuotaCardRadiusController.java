package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.QuotaCardRadius;
import com.value.decision.model.decisionmanage.service.IQuotaCardRadiusService;
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
 * 额度范围表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/quota-card-radius")
public class QuotaCardRadiusController {

    @Autowired
    private IQuotaCardRadiusService quotaCardRadiusService;

    @Log(title = "定额规则新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    public AjaxResult submit(@Validated @RequestBody QuotaCardRadius quotaCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRadiusService.submit(quotaCardRadius, loginUser);
    }

    @Log(title = "定额规则编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    public AjaxResult update(@Validated @RequestBody QuotaCardRadius quotaCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRadiusService.submit(quotaCardRadius, loginUser);
    }

    @RequestMapping("get")
    public AjaxResult get(@RequestBody QuotaCardRadius quotaCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRadiusService.get(quotaCardRadius,loginUser);
    }

}
