package com.risksmart.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.risksmart.system.domain.SysMenu;

/**
 * 菜单表 数据层
 */
@Mapper
public interface SysMenuMapper {

    /**
     * 查询系统菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询所有菜单
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据用户ID查询权限
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据菜单ID查询信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     */
    int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}
