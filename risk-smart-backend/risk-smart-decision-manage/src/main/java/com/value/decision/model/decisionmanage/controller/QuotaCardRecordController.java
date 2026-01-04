package com.value.decision.model.decisionmanage.controller;


import com.value.decision.model.decisionmanage.model.QuotaCardRecord;
import com.value.decision.model.decisionmanage.model.dto.StandardQuotaFormulaDTO;
import com.value.decision.model.decisionmanage.service.IQuotaCardRecordService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 额度卡主表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-12
 */
@RestController
@RequestMapping("/quota-card-record")
public class QuotaCardRecordController {

    @Autowired
    private IQuotaCardRecordService quotaCardRecordService;

    @RequestMapping("list")
    public AjaxResult list(@RequestBody QuotaCardRecord quotaCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRecordService.newList(quotaCardRecord,loginUser);
    }

    @Log(title = "定额规则更新状态", businessType = BusinessType.UPDATE)
    @RequestMapping("updateStatus")
    public AjaxResult updateStatus(@RequestBody QuotaCardRecord quotaCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRecordService.updateStatus(quotaCardRecord);
    }

    //额度发布
    @RequestMapping("release")
    public AjaxResult release(@RequestBody QuotaCardRecord quotaCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return quotaCardRecordService.release(quotaCardRecord, loginUser);
    }

    // ==================== 标准额度公式配置接口 ====================

    /**
     * 保存标准额度计算公式配置
     *
     * 数据流程说明：
     * 1. 前端直接调用中台接口 /integration/treeList 获取字段树（不经过此服务）
     * 2. 前端组装好公式和变量数据后，调用此接口保存配置
     * 3. 后端只负责保存数据和公式校验，不保存快照表
     * 4. 快照表在发布时自动保存（通过 release() 方法）
     *
     * @param formulaDTO 公式配置数据
     * @return 保存结果
     */
    @Log(title = "保存标准额度公式", businessType = BusinessType.UPDATE)
    @PostMapping("/standard-quota-formula")
    public AjaxResult saveStandardQuotaFormula(@Validated @RequestBody StandardQuotaFormulaDTO formulaDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        return quotaCardRecordService.saveStandardQuotaFormula(formulaDTO, loginUser);
    }

    /**
     * 获取标准额度计算公式配置
     *
     * @param quotaCardIdStr 额度卡ID（字符串形式，可能是"null"）
     * @return 公式配置数据
     */
    @GetMapping("/standard-quota-formula/{quotaCardId}")
    public AjaxResult getStandardQuotaFormula(@PathVariable("quotaCardId") String quotaCardIdStr) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }

        // 处理 "null" 字符串或空值的情况（返回200，不算异常）
        if (quotaCardIdStr == null || "null".equals(quotaCardIdStr) || quotaCardIdStr.trim().isEmpty()) {
            return AjaxResult.success("额度模型需要关联评级模型。请先配置评分模型和评级模型，然后再配置额度模型。");
        }

        // 尝试转换为 Integer
        Integer quotaCardId;
        try {
            quotaCardId = Integer.valueOf(quotaCardIdStr);
        } catch (NumberFormatException e) {
            return AjaxResult.error("额度卡ID格式错误：" + quotaCardIdStr);
        }

        return quotaCardRecordService.getStandardQuotaFormula(quotaCardId, loginUser);
    }

}
