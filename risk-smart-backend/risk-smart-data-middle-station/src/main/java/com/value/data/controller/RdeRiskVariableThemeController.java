package com.value.data.controller;

import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.common.utils.StringUtils;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.RdeRiskVariableThemeDTO;
import com.value.data.domain.vo.PageData;
import com.value.data.domain.vo.RdeRiskVariableThemeVO;
import com.value.data.domain.vo.TreeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据主题
 */
@RestController
@Slf4j
@RequestMapping("/metadata/theme")
public class RdeRiskVariableThemeController {
    private ServiceConstant service;

    public RdeRiskVariableThemeController(ServiceConstant service) {
        this.service = service;
    }

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public AjaxResult list(@RequestBody RdeRiskVariableThemeDTO record, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);

        record.setPackageType(null);
        PageData list = null;
        try {
            list = service.rdeRiskVariableThemeService.list(record, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(list);
    }

    @GetMapping("/listAll")
    public AjaxResult listAll() {
        PageData list = new PageData();
        try {
            List<RdeRiskVariableThemeVO> all = service.rdeRiskVariableThemeService.getAll();
            if (all != null && all.size() > 0) {
                list.setRows(all);
                list.setTotal((long) all.size());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(list);
    }

    /**
     * 根据ID查询一个
     */
    @GetMapping(value = "/{themeNo}")
    public AjaxResult getInfo(@PathVariable("themeNo") String themeNo) {
        if (StringUtils.isEmpty(themeNo))
            return AjaxResult.success("themeNo不能为空");

        RdeRiskVariableThemeVO rdeRiskVariableThemeVO = null;
        try {
            rdeRiskVariableThemeVO = service.rdeRiskVariableThemeService.select(themeNo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(rdeRiskVariableThemeVO);
    }

    /**
     * 新增
     */
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody @Validated RdeRiskVariableThemeDTO record, HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        int submit = 0;
        try {
            submit = service.rdeRiskVariableThemeService.submit(record, loginUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(submit);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public AjaxResult update(@RequestBody @Validated RdeRiskVariableThemeDTO record, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (StringUtils.isEmpty(record.getThemeNo()) && StringUtils.isEmpty(record.getInterfaceSourceNo()))
            return AjaxResult.success("themeNo和interfaceSourceNo不能都为空");

        int submit = 0;
        try {
            submit = service.rdeRiskVariableThemeService.update(record, user);
            if (submit == -1)
                return AjaxResult.success("名称不可重复");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(submit);
    }

    @PostMapping("/isExisted")
    public AjaxResult isExisted(@RequestBody RdeRiskVariableThemeDTO record) {
        try {
            boolean existed = service.rdeRiskVariableThemeService.isExisted(record);
            return AjaxResult.success(existed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
    }

    /**
     * 获取树级列表
     */
    @GetMapping("/treeList")
    public AjaxResult treeList() {
        try {
            List<TreeList> treeLists = service.rdeRiskVariableThemeService.treeLists(null);
            return AjaxResult.success(treeLists);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
    }
}
