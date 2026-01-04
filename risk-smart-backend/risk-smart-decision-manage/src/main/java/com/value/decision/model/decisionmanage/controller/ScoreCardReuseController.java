package com.value.decision.model.decisionmanage.controller;


import com.value.decision.common.constant.SecurityConstants;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.dto.model.ScoreCardReuseVO;
import com.value.decision.model.decisionmanage.service.IScoreCardReuseService;
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
 * @since 2024-11-28
 */
@RestController
@RequestMapping("/score-card-reuse")
public class ScoreCardReuseController {

    @Autowired
    private IScoreCardReuseService iScoreCardReuseService;

    /**
     * 标准评分卡复用
     * @param scoreCardReuseVO
     * @return
     */
    @PostMapping("/addStandardModel")
    public AjaxResult addStandardModel(@RequestBody ScoreCardReuseVO scoreCardReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        iScoreCardReuseService.addStandardModel(scoreCardReuseVO,loginUser);
        return AjaxResult.success();
    }

    /**
     * 复用的标准评分卡删除
     * @param scoreCardReuseVO
     * @return
     */
    @PostMapping("/deleteParentCard")
    public AjaxResult deleteParentCard(@RequestBody ScoreCardReuseVO scoreCardReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        iScoreCardReuseService.deleteParentCard(scoreCardReuseVO,loginUser);
        return AjaxResult.success();
    }

    /**
     * 查询已复用的标准评分卡
     * @param scoreCardReuseVO
     * @return
     */
    @PostMapping("/selectParentCard")
    public AjaxResult selectParentCard(@RequestBody ScoreCardReuseVO scoreCardReuseVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        List<ScoreCardRecordVersion> scoreCardRecordVersionList = iScoreCardReuseService.selectStandardModel(scoreCardReuseVO, loginUser);
        return AjaxResult.success(scoreCardRecordVersionList);
    }

}
