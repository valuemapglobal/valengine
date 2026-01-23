package com.value.decision.version.controller;


import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.version.service.ScoreCardRecordVersionService;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评分卡主表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-10
 */
@RestController
@RequestMapping("/score-card-record-version")
public class ScoreCardRecordVersionController {

    @Autowired
    private ScoreCardRecordVersionService scoreCardRecordVersionService;

    /**
     * 评分模型 -- 版本更新
     * @param versionControlVO
     * @return
     */
    @PostMapping("/versionControlFraud")
    public AjaxResult versionControlFraud(@RequestBody VersionControlVO versionControlVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        scoreCardRecordVersionService.versionControlScore(versionControlVO);

        return AjaxResult.success();
    }

    /**
     * 评分模型 -- 保留原版本
     * @param versionControlVO
     * @return
     */
    @PostMapping("/versionReserveScore")
    public AjaxResult versionReserveScore(@RequestBody VersionControlVO versionControlVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        scoreCardRecordVersionService.versionReserveScore(versionControlVO);

        return AjaxResult.success();
    }
}
