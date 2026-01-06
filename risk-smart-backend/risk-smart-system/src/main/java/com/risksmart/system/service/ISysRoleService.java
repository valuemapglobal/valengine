package com.risksmart.system.service;

import java.util.List;
import java.util.Set;
import com.risksmart.system.domain.SysRole;

/**
 * 角色 业务层
 */
public interface ISysRoleService {

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色权限
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 根据角色ID查询角色
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     */
    boolean checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     */
    void checkRoleAllowed(SysRole role);

    /**
     * 校验角色是否有数据权限
     */
    void checkRoleDataScope(Long roleId);

    /**
     * 校验角色是否有数据权限
     */
    void checkRoleDataScope(Long[] roleIds);

    /**
     * 新增保存角色信息
     */
    int insertRole(SysRole role);

    /**
     * 修改保存角色信息
     */
    int updateRole(SysRole role);

    /**
     * 修改角色状态
     */
    int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     */
    int authDataScope(SysRole role);

    /**
     * 批量删除角色信息
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 取消授权用户角色
     */
    int deleteAuthUser(Object userRole);

    /**
     * 批量取消授权用户角色
     */
    int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     */
    int insertAuthUsers(Long roleId, Long[] userIds);
}
