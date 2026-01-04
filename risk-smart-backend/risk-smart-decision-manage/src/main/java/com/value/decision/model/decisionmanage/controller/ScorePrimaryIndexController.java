package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.ScorePrimaryIndex;
import com.value.decision.model.decisionmanage.service.IScorePrimaryIndexService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 一级指标存储表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@RestController
@RequestMapping("/score-primary-index")
public class ScorePrimaryIndexController {

    @Autowired
    private IScorePrimaryIndexService scorePrimaryIndexService;

    @Log(title = "评分卡指标新增", businessType = BusinessType.INSERT)
    @RequestMapping("/submit")
    public AjaxResult submit(@Validated @RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.submit(scorePrimaryIndex, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡指标编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("/update")
    public AjaxResult update(@Validated @RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.submit(scorePrimaryIndex, SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡指标列表查询", businessType = BusinessType.INSERT)
    @RequestMapping("/list")
    public AjaxResult newList(@RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.newList(scorePrimaryIndex, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡指标单个删除", businessType = BusinessType.DELETE)
    @RequestMapping("/delete")
    public AjaxResult delete(@RequestBody Map<String,Object> map) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        if(!map.containsKey("id") || !map.containsKey("parentCardId")){
            return AjaxResult.error("参数缺失");
        }
        return scorePrimaryIndexService.delete(Integer.valueOf(map.get("id").toString()),map.get("parentCardId"), SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡指标卡删除", businessType = BusinessType.DELETE)
    @RequestMapping("/deleteCard")
    public AjaxResult deleteCard(@RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.deleteCard(scorePrimaryIndex.getId(), SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡指标单个查询", businessType = BusinessType.INSERT)
    @RequestMapping("/get/{id}")
    public AjaxResult get(@PathVariable("id") Integer id) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.get(id, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡指标更新状态", businessType = BusinessType.UPDATE)
    @RequestMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.updateStatus(scorePrimaryIndex, SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡指标同名判断", businessType = BusinessType.INSERT)
    @RequestMapping("/checkName")
    public AjaxResult checkName(@RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scorePrimaryIndexService.checkName(scorePrimaryIndex, SecurityUtils.getLoginUser());
    }

}
