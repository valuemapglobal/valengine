package com.risksmart.system.controller;

import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysUser;
import com.risksmart.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 控制器
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        return AjaxResult.success(list);
    }

    /**
     * 根据用户ID获取详细信息
     */
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId) {
        return AjaxResult.success(userService.selectUserById(userId));
    }

    /**
     * 根据用户名查询用户信息（供内部服务调用）
     */
    @PostMapping("/selectOne")
    public AjaxResult selectOne(@RequestBody SysUser user) {
        SysUser sysUser = userService.selectUserByUserName(user.getUserName());
        return AjaxResult.success(sysUser);
    }
}
