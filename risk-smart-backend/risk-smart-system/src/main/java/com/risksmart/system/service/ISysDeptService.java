package com.risksmart.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.domain.vo.TreeSelect;

import java.util.List;

/**
 * 部门管理 服务层
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据部门ID查询信息
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 构建前端所需要下拉树结构
     */
    List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts);

    /**
     * 构建前端所需要树结构
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 查询部门树结构信息
     */
    List<TreeSelect> selectDeptTreeList(SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 校验部门是否有数据权限
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 是否存在子节点
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 查询正常状态的子节点数量
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 新增部门
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门
     */
    int deleteDeptById(Long deptId);
}
