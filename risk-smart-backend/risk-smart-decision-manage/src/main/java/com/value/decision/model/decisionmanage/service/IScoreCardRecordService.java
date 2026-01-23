package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreCardRecordSnapshot;
import com.value.decision.model.decisionmanage.model.vo.ScoreCardRecordVO;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.security.LoginUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 评分卡主表 服务类
 * </p>
 *
 * @author hc
 * @since 2023-08-08
 */
public interface IScoreCardRecordService extends IService<ScoreCardRecord> {

    AjaxResult submit(ScoreCardRecord scoreCardRecord, LoginUser loginUser);

    List<ScoreCardRecord> newlist(ScoreCardRecordVO scoreCardRecord, LoginUser loginUser);

    List<ScoreCardRecord> newBuildList(ScoreCardRecordVO scoreCardRecord, LoginUser loginUser);

    List<ScoreCardRecordSnapshot> newStandardList(ScoreCardRecordVO scoreCardRecordVO, LoginUser loginUser);

    List<ScoreCardRecordSnapshot> rulePoolList(ScoreCardRecordVO scoreCardRecordVO);

    AjaxResult delete(Integer id, LoginUser loginUser);

    AjaxResult get(Integer id, LoginUser loginUser);

    AjaxResult updateStatus(ScoreCardRecord scoreCardRecord, LoginUser loginUser);

    AjaxResult checkName(ScoreCardRecord scoreCardRecord, LoginUser loginUser);

    AjaxResult release(ScoreCardRecord scoreCardRecord,LoginUser loginUser);

    boolean checkIfScoreCardReferencedByProcessPolicy(Integer id);

}
