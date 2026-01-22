package com.risksmart.common.security.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.PatternMatchUtils;
import com.risksmart.common.core.context.SecurityContextHolder;
import com.risksmart.common.core.exception.auth.NotLoginException;
import com.risksmart.common.core.exception.auth.NotPermissionException;
import com.risksmart.common.core.exception.auth.NotRoleException;
import com.risksmart.common.core.utils.SpringUtils;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.security.annotation.Logical;
import com.risksmart.common.security.annotation.RequiresLogin;
import com.risksmart.common.security.annotation.RequiresPermissions;
import com.risksmart.common.security.annotation.RequiresRoles;
import com.risksmart.common.security.service.TokenService;
import com.risksmart.common.security.utils.SecurityUtils;
import com.risksmart.system.api.model.LoginUser;

/**
 * Token 权限验证，逻辑实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class AuthLogic
{
    /** 所有权限标识 */
    private static final String ALL_PERMISSION = "*:*:*";

    /** 管理员角色权限标识 */
    private static final String SUPER_ADMIN = "admin";

    /** TokenService 实例，使用延迟初始化确保 Spring 容器已就绪 */
    private volatile TokenService tokenService;

    /**
     * 获取 TokenService 实例，使用双重检查锁实现线程安全的延迟初始化
     */
    private TokenService getTokenService()
    {
        if (tokenService == null)
        {
            synchronized (this)
            {
                if (tokenService == null)
                {
                    tokenService = SpringUtils.getBean(TokenService.class);
                }
            }
        }
        return tokenService;
    }

    /**
     * 会话注销
     */
    public void logout()
    {
        String token = SecurityUtils.getToken();
        if (token == null)
        {
            return;
        }
        logoutByToken(token);
    }

    /**
     * 会话注销，根据指定Token
     */
    public void logoutByToken(String token)
    {
        getTokenService().delLoginUser(token);
    }

    /**
     * 检验用户是否已经登录，如未登录，则抛出异常
     */
    public void checkLogin()
    {
        getLoginUser();
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     * 
     * @return 用户缓存信息
     */
    public LoginUser getLoginUser()
    {
        String token = SecurityUtils.getToken();
        if (token == null)
        {
            throw new NotLoginException("未提供token");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            throw new NotLoginException("无效的token");
        }
        return loginUser;
    }

    /**
     * 获取当前用户缓存信息, 如果未登录，则抛出异常
     * 
     * @param token 前端传递的认证信息
     * @return 用户缓存信息
     */
    public LoginUser getLoginUser(String token)
    {
        return getTokenService().getLoginUser(token);
    }

    /**
     * 验证当前用户有效期, 如果相差不足120分钟，自动刷新缓存
     * 
     * @param loginUser 当前用户信息
     */
    public void verifyLoginUserExpire(LoginUser loginUser)
    {
        getTokenService().verifyToken(loginUser);
    }

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission)
    {
        return hasPermi(getPermiList(), permission);
    }

    /**
     * 验证用户是否具备某权限, 如果验证未通过，则抛出异常: NotPermissionException
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public void checkPermi(String permission)
    {
        if (!hasPermi(getPermiList(), permission))
        {
            throw new NotPermissionException(permission);
        }
    }

    /**
     * 根据注解(@RequiresPermissions)鉴权, 如果验证未通过，则抛出异常: NotPermissionException
     * 
     * @param requiresPermissions 注解对象
     */
    public void checkPermi(RequiresPermissions requiresPermissions)
    {
        SecurityContextHolder.setPermission(StringUtils.join(requiresPermissions.value(), ","));
        if (requiresPermissions.logical() == Logical.AND)
        {
            checkPermiAnd(requiresPermissions.value());
        }
        else
        {
            checkPermiOr(requiresPermissions.value());
        }
    }

    /**
     * 验证用户是否含有指定权限，必须全部拥有
     *
     * @param permissions 权限列表
     */
    public void checkPermiAnd(String... permissions)
    {
        Set<String> permissionList = getPermiList();
        for (String permission : permissions)
        {
            if (!hasPermi(permissionList, permission))
            {
                throw new NotPermissionException(permission);
            }
        }
    }

    /**
     * 验证用户是否含有指定权限，只需包含其中一个
     * 
     * @param permissions 权限码数组
     */
    public void checkPermiOr(String... permissions)
    {
        Set<String> permissionList = getPermiList();
        for (String permission : permissions)
        {
            if (hasPermi(permissionList, permission))
            {
                return;
            }
        }
        if (permissions.length > 0)
        {
            throw new NotPermissionException(permissions);
        }
    }

    /**
     * 判断用户是否拥有某个角色
     * 
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role)
    {
        return hasRole(getRoleList(), role);
    }

    /**
     * 判断用户是否拥有某个角色, 如果验证未通过，则抛出异常: NotRoleException
     * 
     * @param role 角色标识
     */
    public void checkRole(String role)
    {
        if (!hasRole(role))
        {
            throw new NotRoleException(role);
        }
    }

    /**
     * 根据注解(@RequiresRoles)鉴权
     * 
     * @param requiresRoles 注解对象
     */
    public void checkRole(RequiresRoles requiresRoles)
    {
        if (requiresRoles.logical() == Logical.AND)
        {
            checkRoleAnd(requiresRoles.value());
        }
        else
        {
            checkRoleOr(requiresRoles.value());
        }
    }

    /**
     * 验证用户是否含有指定角色，必须全部拥有
     * 
     * @param roles 角色标识数组
     */
    public void checkRoleAnd(String... roles)
    {
        Set<String> roleList = getRoleList();
        for (String role : roles)
        {
            if (!hasRole(roleList, role))
            {
                throw new NotRoleException(role);
            }
        }
    }

    /**
     * 验证用户是否含有指定角色，只需包含其中一个
     * 
     * @param roles 角色标识数组
     */
    public void checkRoleOr(String... roles)
    {
        Set<String> roleList = getRoleList();
        for (String role : roles)
        {
            if (hasRole(roleList, role))
            {
                return;
            }
        }
        if (roles.length > 0)
        {
            throw new NotRoleException(roles);
        }
    }

    /**
     * 根据注解(@RequiresLogin)鉴权
     * 
     * @param at 注解对象
     */
    public void checkByAnnotation(RequiresLogin at)
    {
        this.checkLogin();
    }

    /**
     * 根据注解(@RequiresRoles)鉴权
     * 
     * @param at 注解对象
     */
    public void checkByAnnotation(RequiresRoles at)
    {
        String[] roleArray = at.value();
        if (at.logical() == Logical.AND)
        {
            this.checkRoleAnd(roleArray);
        }
        else
        {
            this.checkRoleOr(roleArray);
        }
    }

    /**
     * 根据注解(@RequiresPermissions)鉴权
     * 
     * @param at 注解对象
     */
    public void checkByAnnotation(RequiresPermissions at)
    {
        String[] permissionArray = at.value();
        if (at.logical() == Logical.AND)
        {
            this.checkPermiAnd(permissionArray);
        }
        else
        {
            this.checkPermiOr(permissionArray);
        }
    }

    /**
     * 获取当前账号的角色列表
     *
     * @return 角色列表，未登录时返回空集合
     */
    public Set<String> getRoleList()
    {
        try
        {
            LoginUser loginUser = getLoginUser();
            Set<String> roles = loginUser.getRoles();
            return roles != null ? roles : new HashSet<>();
        }
        catch (NotLoginException e)
        {
            // 未登录时返回空集合，这是预期行为
            return new HashSet<>();
        }
    }

    /**
     * 获取当前账号的权限列表
     *
     * @return 权限列表，未登录时返回空集合
     */
    public Set<String> getPermiList()
    {
        try
        {
            LoginUser loginUser = getLoginUser();
            Set<String> permissions = loginUser.getPermissions();
            return permissions != null ? permissions : new HashSet<>();
        }
        catch (NotLoginException e)
        {
            // 未登录时返回空集合，这是预期行为
            return new HashSet<>();
        }
    }

    /**
     * 判断是否包含权限
     * 优化：先快速检查全权限，避免不必要的流处理
     *
     * @param authorities 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(Collection<String> authorities, String permission)
    {
        if (authorities == null || authorities.isEmpty())
        {
            return false;
        }
        // 快速路径：直接检查是否包含全权限标识
        if (authorities.contains(ALL_PERMISSION))
        {
            return true;
        }
        // 快速路径：精确匹配
        if (authorities.contains(permission))
        {
            return true;
        }
        // 慢速路径：使用模式匹配
        for (String authority : authorities)
        {
            if (StringUtils.hasText(authority) && PatternMatchUtils.simpleMatch(authority, permission))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含角色
     * 优化：先快速检查管理员角色，避免不必要的流处理
     *
     * @param roles 角色列表
     * @param role 角色
     * @return 用户是否具备某角色权限
     */
    public boolean hasRole(Collection<String> roles, String role)
    {
        if (roles == null || roles.isEmpty())
        {
            return false;
        }
        // 快速路径：直接检查是否是管理员
        if (roles.contains(SUPER_ADMIN))
        {
            return true;
        }
        // 快速路径：精确匹配
        if (roles.contains(role))
        {
            return true;
        }
        // 慢速路径：使用模式匹配
        for (String r : roles)
        {
            if (StringUtils.hasText(r) && PatternMatchUtils.simpleMatch(r, role))
            {
                return true;
            }
        }
        return false;
    }
}
