package com.value.decision.model.decisionmanage.controller;

import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.service.IScoreIndexRuleService;
import com.value.decision.engine.cache.DroolsKieBaseCache;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 指标规则表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@RestController
@RequestMapping("/score-index-rule")
public class ScoreIndexRuleController {

    private static final Logger logger = LoggerFactory.getLogger(ScoreIndexRuleController.class);

    @Autowired
    private IScoreIndexRuleService scoreIndexRuleService;

    @Autowired
    private DroolsKieBaseCache droolsKieBaseCache;

//    @Log(title = "评分卡规则列表查询", businessType = BusinessType.INSERT)
    @RequestMapping("list")
    public AjaxResult list(@RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.newList(scoreIndexRule, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡规则新增", businessType = BusinessType.INSERT)
    @RequestMapping("submit")
    public AjaxResult submit(@Validated @RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }

        AjaxResult result = scoreIndexRuleService.submit(scoreIndexRule, SecurityUtils.getLoginUser());

        // 清空Drools缓存,确保使用最新规则
        if (result.get("code") != null && (Integer) result.get("code") == 200) {
            droolsKieBaseCache.clearAll();
            logger.info("【评分模型新增】清空Drools缓存: ruleCode={}", scoreIndexRule.getCode());
        }

        return result;
    }

    @Log(title = "评分卡规则编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("update")
    public AjaxResult update(@Validated @RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }

        AjaxResult result = scoreIndexRuleService.newUpdate(scoreIndexRule, SecurityUtils.getLoginUser());

        // 清空Drools缓存,确保使用最新规则
        if (result.get("code") != null && (Integer) result.get("code") == 200) {
            droolsKieBaseCache.clearAll();
            logger.info("【评分模型编辑】清空Drools缓存: ruleCode={}", scoreIndexRule.getCode());
        }

        return result;
    }

//    @Log(title = "评分卡规则验证code唯一", businessType = BusinessType.INSERT)
    @RequestMapping("checkCode")
    public AjaxResult checkCode(@RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.checkCode(scoreIndexRule, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡规则删除", businessType = BusinessType.DELETE)
    @RequestMapping("delete")
    public AjaxResult delete(@RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.delete(scoreIndexRule.getId(), SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡规则排序", businessType = BusinessType.INSERT)
    @RequestMapping("setSort")
    public AjaxResult setSort(@RequestBody List<Map<String,String>> list){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.setSort(list, SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡规则更新状态", businessType = BusinessType.UPDATE)
    @RequestMapping("updateStatus")
    public AjaxResult updateStatus(@RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.updateStatus(scoreIndexRule, SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡规则单个查询", businessType = BusinessType.INSERT)
    @RequestMapping("getById")
    public AjaxResult getById(@RequestBody ScoreIndexRule scoreIndexRule){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreIndexRuleService.get(scoreIndexRule,SecurityUtils.getLoginUser());
    }

}
