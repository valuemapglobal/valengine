package com.value.decision.model.decisionmanage.service;

import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.RuleRecordReuse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.dto.model.RuleRecordReuseVO;
import com.value.decision.model.decisionmanage.model.dto.model.ScoreCardReuseVO;
import com.value.decision.model.rdenew.domain.RdeModelAntiFraud;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.common.security.LoginUser;

import java.util.List;

/**
 * <p>
 * 评分卡复用关联表 服务类
 * </p>
 *
 * @author dianne
 * @since 2024-12-10
 */
public interface IRuleRecordReuseService extends IService<RuleRecordReuse> {

    void addStandardModel(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser);

    void addStandardRuleGroup(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser);

    void addStandardRuleRecord(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser);

    AjaxResult selectStandardModel(RuleRecordReuseVO ruleRecordReuseVO, LoginUser loginUser);

    void deletePolicyGroup(RuleRecordReuseVO ruleRecordReuseVO,LoginUser loginUser);

}
