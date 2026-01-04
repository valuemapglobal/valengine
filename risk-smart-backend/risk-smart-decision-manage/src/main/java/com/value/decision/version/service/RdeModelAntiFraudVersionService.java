package com.value.decision.version.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.version.domain.RdeModelAntiFraudVersion;
import com.value.decision.version.vo.VersionControlVO;

public interface RdeModelAntiFraudVersionService extends IService<RdeModelAntiFraudVersion> {

    void versionControlFraud(VersionControlVO versionControlVO);

    void versionReserveFraud(VersionControlVO versionControlVO);
}
