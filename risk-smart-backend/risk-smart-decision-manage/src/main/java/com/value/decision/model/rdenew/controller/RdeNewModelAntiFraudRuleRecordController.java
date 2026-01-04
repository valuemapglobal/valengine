package com.value.decision.model.rdenew.controller;

import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraudRuleRecord;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudRuleRecordService;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordListVO;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("rdenew/model/antiFraud/rule/record")
public class RdeNewModelAntiFraudRuleRecordController extends BaseController {
    @Autowired
    private RdeModelAntiFraudRuleRecordService service;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;

    @PostMapping("/newList")
    @RequirLoginUser
    public AjaxResult newList(@RequestBody RdeModelAntiFraudRuleRecordVO record){
        List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = service.newList(record);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleRecordList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/newBuildList")
    @RequirLoginUser
    public AjaxResult newBuildList(@RequestBody RdeModelAntiFraudRuleRecordVO record){
        List<RdeModelAntiFraudRuleRecord> rdeModelAntiFraudRuleRecordList = service.newBuildList(record);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleRecordList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/standardList")
    @RequirLoginUser
    public AjaxResult standardList(@RequestBody RdeModelAntiFraudRuleRecordVO record){
        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudRuleRecordList = service.standardList(record);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleRecordList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @PostMapping("/getById")
    public AjaxResult getById(@RequestBody RdeModelAntiFraudRuleRecord record) {
        return AjaxResult.success("成功",service.get(record));
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return AjaxResult.success(service.selectById(id));
    }

    @PostMapping("/submitInsert")
    @Log(title = "反欺诈模型规则组明细", businessType = BusinessType.INSERT)
    public AjaxResult submitInsert(@RequestBody RdeModelAntiFraudRuleRecordListVO rdeModelAntiFraudRuleRecords, HttpServletRequest request) {
        int result = service.submitInsert(rdeModelAntiFraudRuleRecords, request);
        return toAjax(result);
    }

    @PostMapping("/delete")
    @Log(title = "反欺诈模型规则组明细删除", businessType = BusinessType.DELETE)
    public AjaxResult delete(@RequestBody RdeModelAntiFraudRuleRecord record) {
        return AjaxResult.success("删除成功",true);
    }

    @GetMapping("/delete/{id}")
    @Log(title = "反欺诈模型规则组明细删除", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable Integer id) {
        int i = service.deleteById(id);
        return AjaxResult.success("删除成功", i);
    }
}
