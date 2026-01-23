package com.value.data.controller;

import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.dto.SearchInterfaceSourceTreeDTO;
import com.value.data.service.InterfaceSourceManageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 接口数据源控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/interfaceSource")
@Slf4j
@AllArgsConstructor
public class InterfaceSourceController {
    private final InterfaceSourceManageService sourceService;

    @PostMapping("_search/tree")
    public AjaxResult searchInterfaceSourceTree(@RequestBody SearchInterfaceSourceTreeDTO dto, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        return AjaxResult.success(sourceService.pageByTree(dto,user));
    }

    @GetMapping("/quota/interface-tree")
    public AjaxResult quotaInterfaceTree(HttpServletRequest request){
        LoginUser user = SecurityUtils.getLoginUser(request);
        return AjaxResult.success(sourceService.quotaInterfaceTree(user));
    }

}
