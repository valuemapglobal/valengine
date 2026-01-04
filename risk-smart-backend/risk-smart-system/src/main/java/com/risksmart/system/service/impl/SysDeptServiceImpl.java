package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.mapper.SysDeptMapper;
import com.risksmart.system.service.ISysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 部门管理 服务实现
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();

        // 只查询未删除的部门
        wrapper.eq(SysDept::getDelFlag, "0");

        // 只查询正常状态的部门
        wrapper.eq(SysDept::getStatus, "0");

        // 按部门名称模糊查询
        if (dept != null && StringUtils.hasText(dept.getDeptName())) {
            wrapper.like(SysDept::getDeptName, dept.getDeptName());
        }

        // 按父部门ID查询
        if (dept != null && dept.getParentId() != null) {
            wrapper.eq(SysDept::getParentId, dept.getParentId());
        }

        // 按部门ID查询
        if (dept != null && dept.getDeptId() != null) {
            wrapper.eq(SysDept::getDeptId, dept.getDeptId());
        }

        // 排序：先按父部门ID，再按显示顺序
        wrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);

        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return baseMapper.selectById(deptId);
    }
}
