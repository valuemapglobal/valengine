package com.risksmart.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.risksmart.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户管理 数据层
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE user_name = #{userName} AND del_flag = '0'")
    SysUser selectUserByUserName(@Param("userName") String userName);
}
