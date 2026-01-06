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

    /**
     * 新增用户
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     */
    int updateUser(SysUser user);

    /**
     * 批量删除用户
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置密码
     */
    int resetPwd(SysUser user);

    /**
     * 修改用户状态
     */
    int updateUserStatus(SysUser user);

    /**
     * 用户授权角色
     */
    void insertUserAuth(Long userId, Long[] roleIds);
}
