package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.domain.SysUser;
import com.risksmart.system.mapper.SysDeptMapper;
import com.risksmart.system.mapper.SysUserMapper;
import com.risksmart.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 用户管理服务实现
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysDeptMapper deptMapper;

    public SysUserServiceImpl(SysDeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDelFlag, "0");
        if (user != null) {
            if (StringUtils.hasText(user.getUserName())) {
                wrapper.like(SysUser::getUserName, user.getUserName());
            }
            if (StringUtils.hasText(user.getNickName())) {
                wrapper.like(SysUser::getNickName, user.getNickName());
            }
            if (StringUtils.hasText(user.getPhonenumber())) {
                wrapper.like(SysUser::getPhonenumber, user.getPhonenumber());
            }
            if (StringUtils.hasText(user.getStatus())) {
                wrapper.eq(SysUser::getStatus, user.getStatus());
            }
            if (user.getDeptId() != null) {
                wrapper.eq(SysUser::getDeptId, user.getDeptId());
            }
        }
        wrapper.orderByAsc(SysUser::getUserId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysUser selectUserById(Long userId) {
        SysUser user = baseMapper.selectById(userId);
        if (user != null && user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            user.setDept(dept);
        }
        return user;
    }

    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public int insertUser(SysUser user) {
        return baseMapper.insert(user);
    }

    @Override
    public int updateUser(SysUser user) {
        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUserByIds(Long[] userIds) {
        // 逻辑删除
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(SysUser::getUserId, Arrays.asList(userIds));
        wrapper.set(SysUser::getDelFlag, "2");
        return baseMapper.update(null, wrapper);
    }

    @Override
    public int resetPwd(SysUser user) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getUserId, user.getUserId());
        wrapper.set(SysUser::getPassword, user.getPassword());
        wrapper.set(SysUser::getUpdateBy, user.getUpdateBy());
        return baseMapper.update(null, wrapper);
    }

    @Override
    public int updateUserStatus(SysUser user) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getUserId, user.getUserId());
        wrapper.set(SysUser::getStatus, user.getStatus());
        wrapper.set(SysUser::getUpdateBy, user.getUpdateBy());
        return baseMapper.update(null, wrapper);
    }

    @Override
    public void insertUserAuth(Long userId, Long[] roleIds) {
        // 简化实现，暂不处理用户角色关联
    }
}
