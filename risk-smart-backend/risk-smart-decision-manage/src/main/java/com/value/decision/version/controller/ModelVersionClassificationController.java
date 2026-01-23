package com.value.decision.version.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.version.domain.ModelVersionClassification;
import com.value.decision.version.dto.VersionControlDTO;
import com.value.decision.version.service.ModelVersionClassificationService;
import com.value.decision.version.vo.ModelVersionVO;
import com.value.decision.version.vo.VersionControlVO;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 版本归类冠军标识表 前端控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/model-version-classification")
public class ModelVersionClassificationController {

    @Autowired
    private ModelVersionClassificationService modelVersionClassificationService;

    /**
     * 根据模型id能查出全部版本号
     * @param versionControlVO
     * @return
     */
    @PostMapping("/getVersion")
    public AjaxResult getVersion(@RequestBody VersionControlVO versionControlVO){
        LoginUser loginUser = SecurityUtils.getLoginUser();

        VersionControlDTO versionControlDTO = new VersionControlDTO();

        LambdaQueryWrapper<ModelVersionClassification> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ModelVersionClassification::getModelId,versionControlVO.getModelId())
                .eq(ModelVersionClassification::getDataStatus,0)
                .eq(ModelVersionClassification::getRuleCode,versionControlVO.getRuleCode());
        List<ModelVersionClassification> list = modelVersionClassificationService.list(wrapper);

        list.stream().forEach(x ->{
            if (x.getNewVersion() == 1){
                versionControlDTO.setNewVersion(x.getVersionControl());
            }
            if (x.getUserVersion() == 1){
                versionControlDTO.setUserVersion(x.getVersionControl());
            }
        });
        versionControlDTO.setModelId(versionControlVO.getModelId());

        return AjaxResult.success(versionControlDTO);
    }


    @PostMapping("/test")
    public AjaxResult test(@RequestBody ModelVersionVO modelVersionVO, HttpServletRequest request){

        LoginUser loginUser = SecurityUtils.getLoginUser();

        String authorization = request.getHeader("Authorization");
        modelVersionVO.setAuthorization(authorization);
        modelVersionVO.setBusinessCode("1");
        modelVersionVO.setModelId(47256);
        modelVersionVO.setProjectCode("18");
        modelVersionVO.setUserVersion("CCZL-ZR-C-V1.0-20240103-1");
        modelVersionVO.setChampionVersion("MXBBCS-ZR-C-V2.0-20240104-1");
        modelVersionVO.setNewVersion("MXBBCS-ZR-C-V2.0-20240104-1");
        modelVersionVO.setRuleCode("5");
        modelVersionClassificationService.championLogoVersion(modelVersionVO,loginUser);

        return AjaxResult.success();
    }

    @PostMapping("/test01")
    public AjaxResult test01(@RequestBody VersionControlVO versionControlVO){

        return AjaxResult.success(modelVersionClassificationService.getVersion(versionControlVO));
    }


}
