package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.domain.vo.TreeSelect;
import com.risksmart.system.mapper.SysDeptMapper;
import com.risksmart.system.service.ISysDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理 服务实现
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDelFlag, "0");
        if (dept != null) {
            if (org.springframework.util.StringUtils.hasText(dept.getDeptName())) {
                wrapper.like(SysDept::getDeptName, dept.getDeptName());
            }
            if (dept.getParentId() != null) {
                wrapper.eq(SysDept::getParentId, dept.getParentId());
            }
            if (dept.getDeptId() != null) {
                wrapper.eq(SysDept::getDeptId, dept.getDeptId());
            }
            if (org.springframework.util.StringUtils.hasText(dept.getStatus())) {
                wrapper.eq(SysDept::getStatus, dept.getStatus());
            }
        }
        wrapper.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return baseMapper.selectById(deptId);
    }

    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts) {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts) {
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept) {
        List<SysDept> depts = selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        // 简化实现，返回空列表
        return new ArrayList<>();
    }

    @Override
    public void checkDeptDataScope(Long deptId) {
        // 简化实现
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptName, dept.getDeptName());
        wrapper.eq(SysDept::getParentId, dept.getParentId());
        wrapper.eq(SysDept::getDelFlag, "0");
        SysDept info = baseMapper.selectOne(wrapper);
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getParentId, deptId);
        wrapper.eq(SysDept::getDelFlag, "0");
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        // 简化实现
        return false;
    }

    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, "0");
        wrapper.eq(SysDept::getDelFlag, "0");
        wrapper.apply("find_in_set({0}, ancestors)", deptId);
        return Math.toIntExact(baseMapper.selectCount(wrapper));
    }

    @Override
    public int insertDept(SysDept dept) {
        return baseMapper.insert(dept);
    }

    @Override
    public int updateDept(SysDept dept) {
        return baseMapper.updateById(dept);
    }

    @Override
    public int deleteDeptById(Long deptId) {
        return baseMapper.deleteById(deptId);
    }

    private void recursionFn(List<SysDept> list, SysDept t) {
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysDept> list, SysDept t) {
        return !getChildList(list, t).isEmpty();
    }
}
