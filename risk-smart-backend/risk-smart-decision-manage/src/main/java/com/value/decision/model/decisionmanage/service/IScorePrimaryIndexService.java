package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.ScorePrimaryIndex;
import com.value.decision.model.decisionmanage.model.ScorePrimaryIndexSnapshot;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 一级指标存储表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
public interface IScorePrimaryIndexService extends IService<ScorePrimaryIndex> {

    AjaxResult submit(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser);

    AjaxResult newList(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser);

    List<ScorePrimaryIndexSnapshot> rulePoolList(ScorePrimaryIndex scorePrimaryIndex);

    AjaxResult delete(Integer id,Object parentId, LoginUser loginUser);

    AjaxResult deleteCard(Integer id, LoginUser loginUser);

    AjaxResult get(Integer id, LoginUser loginUser);

    AjaxResult updateStatus(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser);

    AjaxResult checkName(ScorePrimaryIndex scorePrimaryIndex, LoginUser loginUser);
}
