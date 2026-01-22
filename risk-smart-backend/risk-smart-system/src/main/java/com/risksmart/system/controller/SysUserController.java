package com.risksmart.system.controller;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.common.core.web.page.TableDataInfo;
import com.risksmart.common.security.utils.SecurityUtils;
import com.risksmart.system.api.model.LoginUser;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.domain.SysRole;
import com.risksmart.system.domain.SysUser;
import com.risksmart.system.service.ISysDeptService;
import com.risksmart.system.service.ISysMenuService;
import com.risksmart.system.service.ISysRoleService;
import com.risksmart.system.service.ISysUserService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户管理控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private static final Logger log = LoggerFactory.getLogger(SysUserController.class);

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysDeptService deptService;
    private final ISysMenuService menuService;

    public SysUserController(ISysUserService userService,
                            @Nullable ISysRoleService roleService,
                            @Nullable ISysDeptService deptService,
                            @Nullable ISysMenuService menuService) {
        this.userService = userService;
        this.roleService = roleService;
        this.deptService = deptService;
        this.menuService = menuService;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysUser user, HttpServletRequest request) {
        // 获取分页参数
        Integer pageNum = 1;
        Integer pageSize = 10;
        try {
            String pageNumStr = request.getParameter("pageNum");
            String pageSizeStr = request.getParameter("pageSize");
            if (pageNumStr != null) {
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (pageSizeStr != null) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (NumberFormatException e) {
            log.warn("分页参数解析失败，使用默认值: pageNum={}, pageSize={}", pageNum, pageSize);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = userService.selectUserList(user);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);

        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(list);
        rspData.setTotal(pageInfo.getTotal());
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 根据部门ID获取用户列表（隐藏敏感信息）
     */
    @GetMapping("/listByDeptIdHidden")
    public AjaxResult listByDeptIdHidden(@RequestParam(value = "deptId", required = false) Long deptId,
                                          @RequestParam(value = "listByDeptIdHidden", required = false) Long deptIdAlt) {
        // 兼容两种参数名
        Long actualDeptId = deptId != null ? deptId : deptIdAlt;
        SysUser queryUser = new SysUser();
        if (actualDeptId != null) {
            queryUser.setDeptId(actualDeptId);
        }
        List<SysUser> list = userService.selectUserList(queryUser);
        // 隐藏敏感信息
        list.forEach(user -> {
            user.setPassword(null);
        });
        return AjaxResult.success(list);
    }

    /**
     * 根据部门ID获取用户列表（隐藏敏感信息）- POST方式
     */
    @PostMapping("/listByDeptIdHidden")
    public AjaxResult listByDeptIdHiddenPost(@RequestBody(required = false) SysUser user) {
        if (user == null) {
            user = new SysUser();
        }
        List<SysUser> list = userService.selectUserList(user);
        // 隐藏敏感信息
        list.forEach(u -> {
            u.setPassword(null);
        });
        return AjaxResult.success(list);
    }

    /**
     * 根据用户ID获取详细信息
     */
    @GetMapping(value = {"/{userId}", "/"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId)) {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
        }
        if (roleService != null) {
            List<SysRole> roles = roleService.selectRoleAll();
            ajax.put("roles", roles);
        }
        return ajax;
    }

    /**
     * 根据用户名查询用户信息（供内部服务调用）
     */
    @PostMapping("/selectOne")
    public AjaxResult selectOne(@RequestBody SysUser user) {
        SysUser sysUser = userService.selectUserByUserName(user.getUserName());
        return AjaxResult.success(sysUser);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/getLoginInfo")
    public AjaxResult getLoginInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("获取用户信息失败");
        }
        // 从数据库获取完整的用户信息（包含部门信息）
        Long userId = loginUser.getSysUser().getUserId();
        SysUser fullUserInfo = userService.selectUserById(userId);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", fullUserInfo != null ? fullUserInfo : loginUser.getSysUser());
        ajax.put("roles", loginUser.getRoles());
        ajax.put("permissions", loginUser.getPermissions());
        return ajax;
    }

    /**
     * 获取用户信息（用于页面展示）
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("获取用户信息失败");
        }

        com.risksmart.system.api.domain.SysUser user = loginUser.getSysUser();
        Set<String> roles = loginUser.getRoles();
        Set<String> permissions = loginUser.getPermissions();

        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 新增用户
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return AjaxResult.toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        user.setUpdateBy(SecurityUtils.getUsername());
        return AjaxResult.toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return AjaxResult.toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return AjaxResult.toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        user.setUpdateBy(SecurityUtils.getUsername());
        return AjaxResult.toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        ajax.put("user", user);
        if (roleService != null) {
            List<SysRole> roles = roleService.selectRolesByUserId(userId);
            ajax.put("roles", roles);
        }
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return AjaxResult.success();
    }

    /**
     * 获取部门树列表
     */
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept) {
        if (deptService != null) {
            return AjaxResult.success(deptService.selectDeptTreeList(dept));
        }
        return AjaxResult.success();
    }
}
