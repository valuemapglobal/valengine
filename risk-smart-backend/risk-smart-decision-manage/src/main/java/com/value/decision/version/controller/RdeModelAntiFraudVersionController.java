package com.value.decision.version.controller;


import com.risksmart.common.core.web.controller.BaseController;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.version.service.RdeModelAntiFraudVersionService;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 反欺诈模型表 前端控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/rde/model/antiFraud/version")
public class RdeModelAntiFraudVersionController extends BaseController {

    @Autowired
    private RdeModelAntiFraudVersionService rdeModelAntiFraudVersionService;

    /**
     * 规则模型 -- 更新版本
     * @param versionControlVO
     * @return
     */
    @PostMapping("/versionControlFraud")
    public AjaxResult versionControlFraud(@RequestBody VersionControlVO versionControlVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        versionControlVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
        rdeModelAntiFraudVersionService.versionControlFraud(versionControlVO);

        return AjaxResult.success();
    }

    /**
     * 规则模型 -- 保留原版本
     * @param versionControlVO
     * @return
     */
    @PostMapping("/versionReserveFraud")
    public AjaxResult versionReserveFraud(@RequestBody VersionControlVO versionControlVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        rdeModelAntiFraudVersionService.versionReserveFraud(versionControlVO);

        return AjaxResult.success();
    }

}
