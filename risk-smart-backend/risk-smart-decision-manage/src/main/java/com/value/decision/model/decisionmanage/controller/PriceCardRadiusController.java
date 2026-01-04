package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.PriceCardRadius;
import com.value.decision.model.decisionmanage.service.IPriceCardRadiusService;
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
 * 定价详情表 前端控制器
 * </p>
 *
 * @author Dianne
 * @since 2023-08-08
 */
@RestController
@RequestMapping("/price-card-radius")
public class PriceCardRadiusController {

    @Autowired
    private IPriceCardRadiusService service;

    @Log(title = "定价规则新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    public AjaxResult submit(@Validated @RequestBody PriceCardRadius priceCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return service.submit(priceCardRadius,loginUser);
    }

    @Log(title = "定价规则编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    public AjaxResult update(@Validated @RequestBody PriceCardRadius priceCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return service.submit(priceCardRadius,loginUser);
    }

    @RequestMapping("get")
    public AjaxResult get(@RequestBody PriceCardRadius priceCardRadius){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return service.get(priceCardRadius);
    }

}
