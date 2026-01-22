package com.value.data.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.entity.FeatureAttributeEntity;
import com.value.data.service.FeatureAttributeService;
import com.value.data.service.FeatureModuleService;
import lombok.AllArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * 特征变量属性 控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/feature-attribute")
@AllArgsConstructor
public class FeatureAttributeController {
    private final FeatureAttributeService featureAttributeService;
    private final FeatureModuleService featureModuleService;

    @PostMapping
    public AjaxResult createFeatureAttribute(@RequestBody @Validated FeatureAttributeEntity featureAttribute,
                                             HttpServletRequest request) {
        final LoginUser loginUser = SecurityUtils.getLoginUser(request);
        //校验
        if (!featureModuleService.checkModuleDept(featureAttribute.getModuleId(),loginUser.getSysUser().getDeptId())){
            return AjaxResult.forbidden("无权操作其它部门数据");
        }
        if (featureAttributeService.isNameOrCodeExists(
                featureAttribute.getModuleId(),
                featureAttribute.getName(),
                featureAttribute.getCode())
        ) {return AjaxResult.conflict("名称或编码已存在");}

        featureAttribute.setUserId(loginUser.getUserid());
        featureAttribute.setDeptId(loginUser.getSysUser().getDeptId());
        featureAttributeService.save(featureAttribute);
        featureModuleService.updateModuleMetadata(featureAttribute.getModuleId());
        return AjaxResult.success("操作成功");
    }


    @GetMapping("/{id}")
    public AjaxResult getFeatureAttribute(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!featureAttributeService.checkDept(id,user.getSysUser().getDeptId())){
            return AjaxResult.forbidden("无权查看其它部门数据");
        }
        FeatureAttributeEntity featureAttribute = featureAttributeService.lambdaQuery()
                .eq(FeatureAttributeEntity::getDeptId,user.getSysUser().getDeptId())
                .eq(FeatureAttributeEntity::getId,id)
                .one();
        return AjaxResult.success("操作成功", featureAttribute);
    }

    @PutMapping
    public AjaxResult updateFeatureAttribute(@RequestBody FeatureAttributeEntity featureAttribute,
                                             HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        Long deptId = user.getSysUser().getDeptId();
        if (!featureModuleService.checkModuleDept(featureAttribute.getModuleId(),deptId) ||
                !featureAttributeService.checkDept(featureAttribute.getId(),deptId)
        ){return AjaxResult.forbidden("无权操作其它部门数据");}
        if (featureAttributeService.isNameOrCodeExistsExcludingId(featureAttribute.getModuleId(),
                featureAttribute.getId(), featureAttribute.getName(), featureAttribute.getCode())) {
            return AjaxResult.error("名称或编码已存在");
        }
        featureAttributeService.updateById(featureAttribute);
        featureModuleService.updateModuleMetadata(featureAttribute.getModuleId());
        return AjaxResult.success("操作成功");
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteFeatureAttribute(@PathVariable Long id, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!featureAttributeService.checkDept(id,user.getSysUser().getDeptId())){
            return AjaxResult.forbidden("无权操作其它部门数据");
        }
        featureAttributeService.removeById(id);
        return AjaxResult.success("操作成功");
    }

    @GetMapping("/list")
    public AjaxResult listFeatureAttributes(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String name,
                                            @RequestParam Long moduleId, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        IPage<FeatureAttributeEntity> featureAttributePage = featureAttributeService.lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .eq(FeatureAttributeEntity::getDeptId,user.getSysUser().getDeptId())
                .like(StrUtil.isNotBlank(name),FeatureAttributeEntity::getName, name)
                .orderByDesc(FeatureAttributeEntity::getCreateTime)
                .page(new Page<>(page, size));
        return AjaxResult.success(featureAttributePage);
    }

    /**
     * 检查名称是否已存在（支持排除指定ID）
     *
     * @param moduleId 模块ID
     * @param name     特征变量属性名称
     * @param id       排除的ID（可选）
     * @param request  HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-name")
    public AjaxResult checkNameExists(@RequestParam Long moduleId,
                                      @RequestParam String name,
                                      @RequestParam(required = false) Long id,
                                      HttpServletRequest request) {
        SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? featureAttributeService.isNameExists(moduleId, name) 
            : featureAttributeService.isNameExistsExcludingId(moduleId, name, id);
        return AjaxResult.success(exists);
    }

    /**
     * 检查编码是否已存在（支持排除指定ID）
     *
     * @param moduleId 模块ID
     * @param code     特征变量属性编码
     * @param id       排除的ID（可选）
     * @param request  HTTP请求
     * @return 是否存在
     */
    @GetMapping("/check-code")
    public AjaxResult checkCodeExists(@RequestParam Long moduleId,
                                      @RequestParam String code,
                                      @RequestParam(required = false) Long id,
                                      HttpServletRequest request) {
        SecurityUtils.getLoginUser(request);
        boolean exists = id == null 
            ? featureAttributeService.isCodeExists(moduleId, code) 
            : featureAttributeService.isCodeExistsExcludingId(moduleId, code, id);
        return AjaxResult.success(exists);
    }

    @PostMapping("/parse-script")
    public AjaxResult parseScript(MultipartFile script, HttpServletRequest request) throws IOException {
        SecurityUtils.getLoginUser(request);

        if (script.isEmpty()) {
            return AjaxResult.error("上传的文件为空");
        }

        // 检查文件类型是否为 .js 或 .txt
        String fileName = script.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".js") && !fileName.endsWith(".txt"))) {
            return AjaxResult.error("仅支持上传 .js 或 .txt 文件");
        }

        // 读取文件内容
        String fileContent = new String(script.getBytes(), StandardCharsets.UTF_8);

        return AjaxResult.success("文件内容读取成功", fileContent);
    }
}
