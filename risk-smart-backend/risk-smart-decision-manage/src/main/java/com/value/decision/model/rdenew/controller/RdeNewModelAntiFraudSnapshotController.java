package com.value.decision.model.rdenew.controller;


import com.value.decision.common.utils.security.SecurityUtils;
import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.snapshot.service.RdeModelAntiFraudSnapshotService;
import com.value.decision.snapshot.vo.*;
import com.risksmart.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 反欺诈模型表 前端控制器
 * </p>
 *
 * @author Dianne
 * @since 2023-05-08
 */
@RestController
@RequestMapping("/rdenew/model/antiFraud/snapshot")
public class RdeNewModelAntiFraudSnapshotController extends BaseController {

    @Autowired
    private RdeModelAntiFraudSnapshotService rdeModelAntiFraudSnapshotService;


    /**
     * 模型策略发布
     * @param ruleReleaseVO
     */
    @PostMapping("/ruleDataRule")
    public AjaxResult ruleDataRule(@RequestBody RuleReleaseVO ruleReleaseVO, HttpServletRequest request){

        try{
            String authorization = request.getHeader("Authorization");
            SysUser user =  SecurityUtils.getLoginUser().getSysUser();
            // 发布完成清除策略测试记录
//            rdeModelAntiFraudSnapshotService.resetTestState(ruleReleaseVO,user.getUserId().intValue());
            rdeModelAntiFraudSnapshotService.ruleDataRule(ruleReleaseVO.getModelIdList(),authorization,user,ruleReleaseVO.getProjectCode(),ruleReleaseVO.getRuleCode(),ruleReleaseVO.getBusinessCode());
        }catch (Exception e){
            return AjaxResult.error("异常:" + e);
        }
        return AjaxResult.success("策略发布成功");
    }

}
