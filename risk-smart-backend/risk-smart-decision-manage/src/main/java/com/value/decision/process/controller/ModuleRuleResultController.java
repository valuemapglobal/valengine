package com.value.decision.process.controller;


import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.process.service.IModuleRuleResultService;
import com.value.decision.process.vo.ProcesPolicyDTO;
import com.value.decision.process.vo.ProcesPolicyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 模块任务结果表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-16
 */
@RestController
@RequestMapping("/module-rule-result")
public class ModuleRuleResultController {

    @Autowired
    private IModuleRuleResultService iModuleRuleResultService;

    /**
     * 回调任务接口 -- 额度结果
     * 0生成中 1生成完成 2审批结果拒绝
     */
    @PostMapping("/quotaResult")
    public AjaxResult quotaResult(@RequestBody ProcesPolicyVO procesPolicyVO){

        ProcesPolicyDTO procesPolicyDTO = iModuleRuleResultService.quotaResult(procesPolicyVO);
        return AjaxResult.success(procesPolicyDTO);

    }

}
