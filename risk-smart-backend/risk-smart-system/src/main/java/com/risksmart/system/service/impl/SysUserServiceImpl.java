package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.domain.SysUser;
import com.risksmart.system.mapper.SysDeptMapper;
import com.risksmart.system.mapper.SysUserMapper;
import com.risksmart.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户管理 服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysDeptMapper deptMapper;

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
}
