package com.risksmart.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.risksmart.system.domain.SysUser;

import java.util.List;

/**
 * 用户管理 服务层
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);
}
