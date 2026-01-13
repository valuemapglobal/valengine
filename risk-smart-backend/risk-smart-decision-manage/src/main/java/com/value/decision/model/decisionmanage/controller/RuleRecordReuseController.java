package com.value.decision.model.decisionmanage.controller;


import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.model.RuleRecordReuseVO;
import com.value.decision.model.decisionmanage.model.dto.model.ScoreCardReuseVO;
import com.value.decision.model.decisionmanage.service.IRuleRecordReuseService;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 评分卡复用关联表 前端控制器
 * </p>
 *
 * @author dianne
 * @since 2024-12-10
 */
@RestController
@RequestMapping("/rule-record-reuse")
public class RuleRecordReuseController {

    @Autowired
    private IRuleRecordReuseService ruleRecordReuseService;

    /**
     * 策略复用
     * @param ruleRecordReuseVO
     * @return
     */
    @PostMapping("/addStandardModel")
    public AjaxResult addStandardModel(@RequestBody RuleRecordReuseVO ruleRecordReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        ruleRecordReuseService.addStandardModel(ruleRecordReuseVO,loginUser);
        return AjaxResult.success();
    }


    /**
     * 规则组复用
     * @param ruleRecordReuseVO
     * @return
     */
    @PostMapping("/addStandardRuleGroup")
    public AjaxResult addStandardRuleGroup(@RequestBody RuleRecordReuseVO ruleRecordReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        ruleRecordReuseService.addStandardRuleGroup(ruleRecordReuseVO,loginUser);
        return AjaxResult.success();
    }


    /**
     * 规则复用
     * @param ruleRecordReuseVO
     * @return
     */
    @PostMapping("/addStandardRuleRecord")
    public AjaxResult addStandardRuleRecord(@RequestBody RuleRecordReuseVO ruleRecordReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        ruleRecordReuseService.addStandardRuleRecord(ruleRecordReuseVO,loginUser);
        return AjaxResult.success();
    }


    /**
     * 查询已复用的标准策略
     * @param ruleRecordReuseVO
     * @return
     */
    @PostMapping("/selectPolicyGroup")
    public AjaxResult selectParentCard(@RequestBody RuleRecordReuseVO ruleRecordReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        return ruleRecordReuseService.selectStandardModel(ruleRecordReuseVO, loginUser);
    }


    /**
     * 引用的标准数据删除
     * @param ruleRecordReuseVO
     * @return
     */
    @PostMapping("/deletePolicyGroup")
    public AjaxResult deletePolicyGroup(@RequestBody RuleRecordReuseVO ruleRecordReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        ruleRecordReuseService.deletePolicyGroup(ruleRecordReuseVO, loginUser);
        return AjaxResult.success();
    }

}
