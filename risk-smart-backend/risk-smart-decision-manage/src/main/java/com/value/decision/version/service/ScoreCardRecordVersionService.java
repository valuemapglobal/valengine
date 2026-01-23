package com.value.decision.version.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.version.domain.ScoreCardRecordVersion;
import com.value.decision.version.vo.VersionControlVO;

public interface ScoreCardRecordVersionService extends IService<ScoreCardRecordVersion> {

    void versionControlScore(VersionControlVO versionControlVO);

    void versionReserveScore(VersionControlVO versionControlVO);
}
