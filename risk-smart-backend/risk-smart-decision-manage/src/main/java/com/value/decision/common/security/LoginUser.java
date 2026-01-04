package com.value.decision.common.security;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息
 * 替代RuoYi框架的LoginUser
 *
 * @author OP-Lite Team
 * @since 1.0.0
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * Token
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 用户信息
     */
    private com.risksmart.system.domain.SysUser sysUser;

    public LoginUser() {
    }

    public LoginUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserid() {
        return userId;
    }

    public void setUserid(Long userid) {
        this.userId = userid;
    }
}
