package com.value.decision.model.decisionmanage.service;

import com.value.decision.model.decisionmanage.model.ScoreCardRecord;
import com.value.decision.model.decisionmanage.model.ScoreCardReuse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.model.decisionmanage.model.dto.model.ScoreCardReuseVO;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.common.security.LoginUser;

import java.util.List;

/**
 * 评分卡复用关联表 服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface IScoreCardReuseService extends IService<ScoreCardReuse> {

    void addStandardModel(ScoreCardReuseVO scoreCardReuseVO,LoginUser loginUser);

    void deleteParentCard(ScoreCardReuseVO scoreCardReuseVO,LoginUser loginUser);

    List<ScoreCardRecordVersion> selectStandardModel(ScoreCardReuseVO scoreCardReuseVO, LoginUser loginUser);

}
