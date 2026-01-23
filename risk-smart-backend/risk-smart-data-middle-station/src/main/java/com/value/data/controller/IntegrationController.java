package com.value.data.controller;

import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.service.IntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 联调控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@Slf4j
@RequestMapping("/integration")
public class IntegrationController {
    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * 获取树级列表并携带关联的接口信息
     */
    @GetMapping("/treeList")
    public AjaxResult treeList(HttpServletRequest request){
        LoginUser user = SecurityUtils.getLoginUser(request);
        Map<String, Object> tree = integrationService.tree(user.getSysUser().getDeptId());
        return AjaxResult.success(tree);
    }

}
