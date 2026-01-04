package com.value.decision.version.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.dto.VersionControlDTO;
import com.value.decision.version.vo.ModelVersionVO;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.security.LoginUser;

import java.util.List;

public interface ModelVersionClassificationService extends IService<ModelVersionClassification> {

    public void championLogoVersion(ModelVersionVO modelVersionVO, LoginUser loginUser);

    public List<VersionControlDTO> getVersion(VersionControlVO versionControlVO);
}
