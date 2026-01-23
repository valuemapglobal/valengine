package com.value.decision.model.decisionmanage.controller;


import com.value.decision.framework.aspectj.lang.annotation.Log;
import com.value.decision.framework.aspectj.lang.enums.BusinessType;
import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreCardRecordSnapshot;
import com.value.decision.model.decisionmanage.model.vo.ScoreCardRecordVO;
import com.value.decision.model.decisionmanage.service.IScoreCardRecordService;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 评分卡主表 前端控制器
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
@RestController
@RequestMapping("/score-card-record")
public class ScoreCardRecordController {

    @Autowired
    private IScoreCardRecordService scoreCardRecordService;

    @Autowired
    private CommonRuleFunctionDataNew commonRuleFunctionDataNew;


    @Log(title = "评分卡模型新增", businessType = BusinessType.INSERT)
    @RequestMapping("/submit")
    public AjaxResult submit(@Validated @RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.submit(scoreCardRecord,SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡模型编辑", businessType = BusinessType.UPDATE)
    @RequestMapping("/update")
    public AjaxResult update(@Validated @RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.submit(scoreCardRecord,SecurityUtils.getLoginUser());
    }


//    @Log(title = "评分卡模型列表查询", businessType = BusinessType.INSERT)
    @RequestMapping("/list")
    public AjaxResult list( @RequestBody ScoreCardRecordVO scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordService.newlist(scoreCardRecord, SecurityUtils.getLoginUser());
        AjaxResult paging = commonRuleFunctionDataNew.paging(scoreCardRecordList, scoreCardRecord.getPageNum(), scoreCardRecord.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    /**
     * 规则复用-自建列表
     * @param scoreCardRecord
     * @return
     */
    @RequestMapping("/newBuildList")
    public AjaxResult newBuildList( @RequestBody ScoreCardRecordVO scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        List<ScoreCardRecord> scoreCardRecordList = scoreCardRecordService.newBuildList(scoreCardRecord, SecurityUtils.getLoginUser());
        AjaxResult paging = commonRuleFunctionDataNew.paging(scoreCardRecordList, scoreCardRecord.getPageNum(), scoreCardRecord.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    /**
     * 规则复用-标准列表
     * @param scoreCardRecord
     * @return
     */
    @RequestMapping("/standardList")
    public AjaxResult standardList( @RequestBody ScoreCardRecordVO scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        List<ScoreCardRecordSnapshot> scoreCardRecordsList = scoreCardRecordService.newStandardList(scoreCardRecord, SecurityUtils.getLoginUser());
        AjaxResult paging = commonRuleFunctionDataNew.paging(scoreCardRecordsList, scoreCardRecord.getPageNum(), scoreCardRecord.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @Log(title = "评分卡模型删除", businessType = BusinessType.DELETE)
    @RequestMapping("/delete")
    public AjaxResult checkDelete(@RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }

        // 检查评分卡是否被流程策略引用
        boolean isReferenced = scoreCardRecordService.checkIfScoreCardReferencedByProcessPolicy(scoreCardRecord.getId());
        if (isReferenced) {
            return AjaxResult.success("检测到目前正在被流程使用，无法删除",false);
        }
        scoreCardRecordService.delete(scoreCardRecord.getId(), SecurityUtils.getLoginUser());
        return AjaxResult.success("操作成功",true);
    }


//    @Log(title = "评分卡模型单个查询", businessType = BusinessType.INSERT)
    @RequestMapping("/get/{id}")
    public AjaxResult get( @PathVariable("id") Integer id){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.get(id,SecurityUtils.getLoginUser());
    }

    @Log(title = "评分卡模型编辑启用状态", businessType = BusinessType.ENABLE)
    @RequestMapping("/updateStatus")
    public AjaxResult updateStatus( @RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.updateStatus(scoreCardRecord,SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡模型同名判断", businessType = BusinessType.INSERT)
    @RequestMapping("/checkName")
    public AjaxResult checkName( @RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.checkName(scoreCardRecord,SecurityUtils.getLoginUser());
    }

//    @Log(title = "评分卡模型发布", businessType = BusinessType.USE)
    @RequestMapping("release")
    public AjaxResult release(@RequestBody ScoreCardRecord scoreCardRecord){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(loginUser == null){
            return AjaxResult.error("用户未登录");
        }
        return scoreCardRecordService.release(scoreCardRecord,loginUser);
    }

}
