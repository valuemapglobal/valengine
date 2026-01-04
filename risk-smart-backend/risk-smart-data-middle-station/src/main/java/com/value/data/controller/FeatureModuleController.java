package com.value.data.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.entity.FeatureModuleEntity;
import com.value.data.service.FeatureModuleService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 特征变量模块表 前端控制器
 * </p>
 *
 * @author Vida
 * @since 2025-04-15
 */
@RestController
@RequestMapping("/feature-module")
@AllArgsConstructor
public class FeatureModuleController {
    private final FeatureModuleService featureModuleService;

    /**
     * 添加特征变量模块
     * name或code不能重复
     */
    @PostMapping
    public AjaxResult createFeatureModule(@RequestBody @Validated FeatureModuleEntity featureModule,
                                          HttpServletRequest request) {
        final LoginUser user = SecurityUtils.getLoginUser(request);
        //校验
        if (featureModuleService.isNameOrCodeExists(featureModule.getName(),
                featureModule.getCode(),
                null,
                user.getSysUser().getDeptId())
        ) {return AjaxResult.error("名称或编码已存在");}
        //填充数据并保存
        featureModule.setUserId(user.getUserid());
        featureModule.setDeptId(user.getSysUser().getDeptId());
        featureModuleService.save(featureModule);
        return AjaxResult.success();
    }

    @GetMapping("/{id}")
    public AjaxResult getFeatureModule(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!featureModuleService.checkModuleDept(id, user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权查看其它部门数据");
        }
        FeatureModuleEntity featureModule = featureModuleService.getById(id);
        return AjaxResult.success( featureModule);
    }

    @PutMapping
    public AjaxResult updateFeatureModule(@RequestBody FeatureModuleEntity featureModule,
                                          HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!featureModuleService.checkModuleDept(featureModule.getId(), user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权操作其它部门数据");
        }
        // 检查是否存在相同名称或编码的特征变量模块（排除当前记录）
        if (featureModuleService.isNameOrCodeExists(featureModule.getName(),
                featureModule.getCode(),
                featureModule.getId(),
                user.getSysUser().getDeptId())
        ) {return AjaxResult.error("名称或编码已存在");}
        featureModuleService.lambdaUpdate()
                .eq(FeatureModuleEntity::getId,featureModule.getId())
                .update(featureModule);
        return AjaxResult.success();
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteFeatureModule(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!featureModuleService.checkModuleDept(id, user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权操作其它部门数据");
        }
        featureModuleService.deleteFeatureModule(id);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    public AjaxResult listFeatureModules(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String name,
                                         HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        IPage<FeatureModuleEntity> featureModulePage = featureModuleService.lambdaQuery()
                .eq(FeatureModuleEntity::getDeptId,user.getSysUser().getDeptId())
                .like(StringUtils.isNotBlank(name), FeatureModuleEntity::getName, name)
                .orderByDesc(FeatureModuleEntity::getCreateTime)
                .page(new Page<>(page, size));
        return AjaxResult.success(featureModulePage);
    }

    /**
     * 检查名称是否已存在（支持排除指定ID）
     *
     * @param name    特征变量模块名称
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-name")
    public AjaxResult checkNameExists(@RequestParam String name,
                                  @RequestParam(required = false) Long id,
                                  HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        boolean exists = featureModuleService.isNameExists(name,id,user.getSysUser().getDeptId());
        return AjaxResult.success(exists);
    }

    /**
     * 检查编码是否已存在（支持排除指定ID）
     *
     * @param code    特征变量模块编码
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-code")
    public AjaxResult checkCodeExists(@RequestParam String code,
                                  @RequestParam(required = false) Long id,
                                  HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        boolean exists = featureModuleService.isCodeExists(code,id,user.getSysUser().getDeptId());
        return AjaxResult.success(exists);
    }
}