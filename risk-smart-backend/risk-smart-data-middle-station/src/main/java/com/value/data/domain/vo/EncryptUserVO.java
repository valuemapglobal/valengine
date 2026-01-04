package com.value.data.domain.vo;

import lombok.Data;

/**
 * 效验、用户信息
 */
@Data
public class EncryptUserVO {

    /**
     * 密匙 ID
     */
    private String appKey;

    /**
     * 密匙
     */
    private String secret;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 部门编号
     */
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户昵称
     */
    private String nickName;
}
