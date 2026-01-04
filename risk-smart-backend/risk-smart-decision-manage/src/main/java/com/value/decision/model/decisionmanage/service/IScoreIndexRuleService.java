package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.ScoreIndexRule;
import com.value.decision.model.decisionmanage.model.ScoreIndexRuleSnapshot;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 指标规则表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
public interface IScoreIndexRuleService extends IService<ScoreIndexRule> {

    AjaxResult newList(ScoreIndexRule scoreIndexRule, LoginUser loginUser);

    AjaxResult submit(ScoreIndexRule scoreIndexRule, LoginUser loginUser);

    AjaxResult newUpdate(ScoreIndexRule scoreIndexRule, LoginUser loginUser);

    AjaxResult checkCode(ScoreIndexRule scoreIndexRule, LoginUser loginUser);

    AjaxResult delete(Integer id, LoginUser loginUser);

    AjaxResult setSort(List<Map<String, String>> list, LoginUser loginUser);

    AjaxResult updateStatus(ScoreIndexRule scoreIndexRule, LoginUser loginUser);

    AjaxResult get(ScoreIndexRule id, LoginUser loginUser);

    List<ScoreIndexRuleSnapshot> rulePoolList(ScoreIndexRule scoreIndexRule);
}
