package com.risksmart.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.risksmart.system.domain.SysDept;

import java.util.List;

/**
 * 部门管理 服务层
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表
     *
     * @param dept 部门查询条件
     * @return 部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDept selectDeptById(Long deptId);
}
