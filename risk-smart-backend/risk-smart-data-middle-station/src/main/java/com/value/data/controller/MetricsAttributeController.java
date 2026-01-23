package com.value.data.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.entity.MetricsAttributeEntity;
import com.value.data.service.MetricsAttributeService;
import com.value.data.service.MetricsModuleService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 分析指标属性 控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/metrics-attribute")
@AllArgsConstructor
public class MetricsAttributeController {

    private final MetricsAttributeService metricsAttributeService;
    private final MetricsModuleService moduleService;

    /**
     * 添加分析指标属性
     *
     * @param metricsAttribute 属性实体
     * @param request          请求对象
     * @return 操作结果
     */
    @PostMapping
    public AjaxResult createMetricsAttribute(@RequestBody @Validated MetricsAttributeEntity metricsAttribute,
                                             HttpServletRequest request) {
        final LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //校验
        if (!moduleService.checkModuleDept(metricsAttribute.getModuleId(), loginUser.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权操作其他部门数据");
        }
        if (metricsAttributeService.isNameOrCodeExists(metricsAttribute.getModuleId(),
                metricsAttribute.getName(),
                metricsAttribute.getCode())) {
            return AjaxResult.error("名称或编码已存在");
        }
        if (!metricsAttributeService.checkFormula(metricsAttribute)) {
            return AjaxResult.error("公式预执行异常，请检查公式格式");
        }
        metricsAttribute.setUserId(loginUser.getUserid());
        metricsAttribute.setDeptId(loginUser.getSysUser().getDeptId());
        metricsAttributeService.save(metricsAttribute);
        moduleService.updateModuleAssociation(metricsAttribute.getModuleId());
        return AjaxResult.success();
    }

    /**
     * 查询单个分析指标属性
     *
     * @param id      属性ID
     * @param request 请求对象
     * @return 操作结果
     */
    @GetMapping("/{id}")
    public AjaxResult getMetricsAttribute(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        //校验
        if (!metricsAttributeService.checkAttributeDept(id, user.getSysUser().getDeptId())) {
            return AjaxResult.forbidden("无权查看其他部门数据");
        }
        MetricsAttributeEntity metricsAttribute = metricsAttributeService.getById(id);
        return AjaxResult.success(metricsAttribute);
    }

    /**
     * 更新分析指标属性
     *
     * @param metricsAttribute 属性实体
     * @param request          请求对象
     * @return 操作结果
     */
    @PutMapping
    public AjaxResult updateMetricsAttribute(@RequestBody MetricsAttributeEntity metricsAttribute,
                                             HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        //校验数据权限
        if (!metricsAttributeService.checkAttributeDept(metricsAttribute.getModuleId(), user.getSysUser().getDeptId())){
            return AjaxResult.forbidden("无权操作其他部门数据");
        }
        // 检查是否存在相同名称或编码的分析指标属性（排除当前记录）
        if (metricsAttributeService.isNameOrCodeExistsExcludingId(metricsAttribute.getModuleId(),
                metricsAttribute.getName(),
                metricsAttribute.getCode(),
                metricsAttribute.getId())) {
            return AjaxResult.error("名称或编码已存在");
        }
        //校验公式
        if (!metricsAttributeService.checkFormula(metricsAttribute)) {
            return AjaxResult.error("公式预执行异常，请检查公式格式");
        }
        metricsAttributeService.updateById(metricsAttribute);
        moduleService.updateModuleAssociation(metricsAttribute.getModuleId());
        return AjaxResult.success();
    }

    /**
     * 删除分析指标属性
     *
     * @param id      属性ID
     * @param request 请求对象
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteMetricsAttribute(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!metricsAttributeService.checkAttributeDept(id, user.getSysUser().getDeptId())){
            return AjaxResult.forbidden("无权操作其他部门数据");
        }
        metricsAttributeService.removeById(id);
        return AjaxResult.success();
    }

    /**
     * 分页查询分析指标属性列表
     *
     * @param page    当前页码
     * @param size    每页大小
     * @param name    属性名称（可选）
     * @param request 请求对象
     * @return 操作结果
     */
    @GetMapping("/list")
    public AjaxResult listMetricsAttributes(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam Long moduleId,
                                            @RequestParam(required = false) String name,
                                            HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        IPage<MetricsAttributeEntity> metricsAttributePage = metricsAttributeService.lambdaQuery()
                .eq(MetricsAttributeEntity::getModuleId, moduleId)
                .eq(MetricsAttributeEntity::getDeptId, user.getSysUser().getDeptId())
                .like(StringUtils.isNotBlank(name), MetricsAttributeEntity::getName, name)
                .orderByDesc(MetricsAttributeEntity::getCreateTime)
                .page(new Page<>(page, size));
        return AjaxResult.success(metricsAttributePage);
    }

    /**
     * 检查名称是否已存在（支持排除指定ID）
     *
     * @param name    分析指标属性名称
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-name")
    public AjaxResult checkNameExists(@RequestParam String name,
                                  @RequestParam(required = false) Long id,
                                  @RequestParam Long moduleId,
                                  HttpServletRequest request) {
        SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? metricsAttributeService.isNameExists(moduleId,name)
            : metricsAttributeService.isNameExistsExcludingId(moduleId,name, id);
        return AjaxResult.success(exists);
    }

    /**
     * 检查编码是否已存在（支持排除指定ID）
     *
     * @param code    分析指标属性编码
     * @param id      排除的ID（可选）
     * @param request HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-code")
    public AjaxResult checkCodeExists(@RequestParam String code,
                                  @RequestParam(required = false) Long id,
                                  @RequestParam Long moduleId,
                                  HttpServletRequest request) {
        SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? metricsAttributeService.isCodeExists(moduleId,code)
            : metricsAttributeService.isCodeExistsExcludingId(moduleId,code, id);
        return AjaxResult.success(exists);
    }
}