package com.risksmart.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.risksmart.system.domain.SysRole;

/**
 * 角色表 数据层
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Mapper
public interface SysRoleMapper {

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleAll();

    /**
     * 根据用户ID查询角色
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色权限
     */
    List<String> selectRolePermissionByUserId(Long userId);

    /**
     * 根据角色ID查询角色
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 校验角色名称是否唯一
     */
    SysRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     */
    SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 新增角色信息
     */
    int insertRole(SysRole role);

    /**
     * 修改角色信息
     */
    int updateRole(SysRole role);

    /**
     * 删除角色信息
     */
    int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     */
    int deleteRoleByIds(Long[] roleIds);
}
