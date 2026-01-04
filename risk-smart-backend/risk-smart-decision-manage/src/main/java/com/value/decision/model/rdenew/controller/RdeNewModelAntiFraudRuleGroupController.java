package com.value.decision.model.rdenew.controller;

import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudRuleGroupVO;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleGroup;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudRuleGroupService;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleGroupVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("rdenew/model/antiFraud/rule/group")
public class RdeNewModelAntiFraudRuleGroupController extends BaseController {
    @Autowired
    private RdeModelAntiFraudRuleGroupService service;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @PostMapping("/newList")
    @RequirLoginUser
    public AjaxResult newList(@RequestBody ModelAntiFraudRuleGroupVO record, HttpServletRequest request){
        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = service.newList(record, request);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleGroupList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/newBuildList")
    @RequirLoginUser
    public AjaxResult newBuildList(@RequestBody ModelAntiFraudRuleGroupVO record, HttpServletRequest request){
        List<RdeModelAntiFraudRuleGroup> rdeModelAntiFraudRuleGroupList = service.newBuildList(record, request);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleGroupList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/standardList")
    @RequirLoginUser
    public AjaxResult standardList(@RequestBody ModelAntiFraudRuleGroupVO record, HttpServletRequest request){
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupList = service.standardList(record, request);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleGroupList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/getById")
    public AjaxResult getById(@RequestBody RdeModelAntiFraudRuleGroup record) {
        return AjaxResult.success("成功",service.get(record));
    }
    @GetMapping("/rules/{id}")
    public AjaxResult getRules(@PathVariable Integer id) {
        return AjaxResult.success(service.getRules(id));
    }



    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return AjaxResult.success(service.selectById(id));
    }

    @PostMapping("/submit")
    @Log(title = "反欺诈模型规则组新增", businessType = BusinessType.INSERT)
    public AjaxResult submit(@Validated @RequestBody RdeModelAntiFraudRuleGroupVO record) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        int result = service.submit(record, null);
        return toAjax(result);
    }

    @PostMapping("/update")
    @Log(title = "反欺诈模型规则组编辑", businessType = BusinessType.UPDATE)
    public AjaxResult update(@Validated @RequestBody RdeModelAntiFraudRuleGroupVO record) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return toAjax(service.submit(record,null));
    }

    @PostMapping("/delete")
    @Log(title = "反欺诈模型规则组删除", businessType = BusinessType.DELETE)
    public AjaxResult delete(@RequestBody RdeModelAntiFraudRuleGroup record) {
        return toAjax(service.delete(record));
    }

    @GetMapping("/delete/{id}")
    @Log(title = "反欺诈模型规则组删除", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable Integer id) {
        return toAjax(service.deleteById(id));
    }

}
