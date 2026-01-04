package com.value.data.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.entity.MetricsModuleEntity;
import com.value.data.service.MetricsModuleService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * 分析指标模块表 前端控制器
 * </p>
 *
 * @author Vida
 * @since 2025-04-23
 */
@RestController
@RequestMapping("/metrics-module")
@AllArgsConstructor
public class MetricsModuleController {
    private final MetricsModuleService metricsModuleService;

    /**
     * 添加分析指标模块
     * name或code不能重复
     *
     */
    @PostMapping
    public AjaxResult createMetricsModule(@RequestBody @Validated MetricsModuleEntity metricsModule,
                                          HttpServletRequest request) {
        final LoginUser loginUser = SecurityUtils.getLoginUser(request);
        if (metricsModuleService.isNameOrCodeExists(
                metricsModule.getName(),
                metricsModule.getCode(),
                null,
                loginUser.getSysUser().getDeptId())
        ) {return AjaxResult.conflict("名称或编码已存在");}

        metricsModule.setUserId(loginUser.getUserid());
        metricsModule.setDeptId(loginUser.getSysUser().getDeptId());
        metricsModuleService.save(metricsModule);
        return AjaxResult.success();
    }

    /**
     * 查询单个分析指标模块
     *
     */
    @GetMapping("/{id}")
    public AjaxResult getMetricsModule(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!metricsModuleService.checkModuleDept(id, user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权查看其他部门数据");
        }
        MetricsModuleEntity metricsModule = metricsModuleService.getById(id);
        return AjaxResult.success(metricsModule);
    }

    /**
     * 更新分析指标模块
     *
     */
    @PutMapping
    public AjaxResult updateMetricsModule(@RequestBody MetricsModuleEntity metricsModule,
                                          HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!metricsModuleService.checkModuleDept(metricsModule.getId(), user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权修改其他部门数据");
        }
        // 检查是否存在相同名称或编码的分析指标模块（排除当前记录）
        if (metricsModuleService.isNameOrCodeExists(
                metricsModule.getName(),
                metricsModule.getCode(),
                metricsModule.getId(),
                user.getSysUser().getDeptId())
        ) {return AjaxResult.error("名称或编码已存在");}
        metricsModuleService.updateById(metricsModule);
        return AjaxResult.success();
    }

    /**
     * 删除分析指标模块
     *
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteMetricsModule(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!metricsModuleService.checkModuleDept(id, user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权操作其他部门数据");
        }
        metricsModuleService.removeById(id);
        return AjaxResult.success();
    }

    /**
     * 分页查询分析指标模块列表
     *
     */
    @GetMapping("/list")
    public AjaxResult listMetricsModules(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String name,
                                         HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        IPage<MetricsModuleEntity> metricsModulePage = metricsModuleService.lambdaQuery()
                .eq(MetricsModuleEntity::getDeptId, user.getSysUser().getDeptId())
                .like(StringUtils.isNotBlank(name), MetricsModuleEntity::getName, name)
                .orderByDesc(MetricsModuleEntity::getCreateTime)
                .page(new Page<>(page, size));
        return AjaxResult.success(metricsModulePage);
    }

    /**
     * 检查名称是否已存在（支持排除指定ID）
     *
     * @param name    分析指标模块名称
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-name")
    public AjaxResult checkNameExists(@RequestParam String name,
                                  @RequestParam(required = false) Long id,
                                  HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? metricsModuleService.isNameExists(name,null,user.getSysUser().getDeptId())
            : metricsModuleService.isNameExists(name, id,user.getSysUser().getDeptId());
        return AjaxResult.success(exists);
    }

    /**
     * 检查编码是否已存在（支持排除指定ID）
     *
     * @param code    分析指标模块编码
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-code")
    public AjaxResult checkCodeExists(@RequestParam String code,
                                  @RequestParam(required = false) Long id,
                                  HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? metricsModuleService.isCodeExists(code, null,user.getSysUser().getDeptId())
            : metricsModuleService.isCodeExists(code, id,user.getSysUser().getDeptId());
        return AjaxResult.success(exists);
    }
}