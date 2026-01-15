package com.risksmart.system.controller;

import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.common.core.domain.R;
import com.risksmart.system.api.model.LoginUser;
import com.risksmart.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息 内部服务调用
 */
@RestController
@RequestMapping("/user")
public class SysUserInnerController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username,
                             @RequestHeader(value = SecurityConstants.FROM_SOURCE, required = false) String source) {
        com.risksmart.system.domain.SysUser sysUser = userService.selectUserByUserName(username);
        if (sysUser == null) {
            return R.fail("用户名或密码错误");
        }

        // 转换为 API 层的 SysUser
        com.risksmart.system.api.domain.SysUser apiSysUser = new com.risksmart.system.api.domain.SysUser();
        BeanUtils.copyProperties(sysUser, apiSysUser);

        // 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(sysUser.getUserId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setSysUser(apiSysUser);

        // 简化权限处理：返回管理员权限
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        loginUser.setRoles(roles);

        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        loginUser.setPermissions(permissions);

        return R.ok(loginUser);
    }

    /**
     * 注册用户信息
     */
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody com.risksmart.system.api.domain.SysUser sysUser,
                               @RequestHeader(value = SecurityConstants.FROM_SOURCE, required = false) String source) {
        // 暂不实现
        return R.fail("暂不支持注册");
    }

    /**
     * 记录用户登录信息
     */
    @PutMapping("/recordlogin")
    public R<Boolean> recordLogin(@RequestBody com.risksmart.system.api.domain.SysUser sysUser,
                                  @RequestHeader(value = SecurityConstants.FROM_SOURCE, required = false) String source) {
        // 暂不实现
        return R.ok(true);
    }

    /**
     * 根据用户ID查询用户信息
     */
    @GetMapping("/queryById/{userId}")
    public R<Object> queryUserById(@PathVariable("userId") Integer userId) {
        com.risksmart.system.domain.SysUser sysUser = userService.selectUserById(userId.longValue());
        if (sysUser == null) {
            return R.fail("用户不存在");
        }
        // 转换为 API 层的 SysUser
        com.risksmart.system.api.domain.SysUser apiSysUser = new com.risksmart.system.api.domain.SysUser();
        BeanUtils.copyProperties(sysUser, apiSysUser);
        return R.ok(apiSysUser);
    }
}
