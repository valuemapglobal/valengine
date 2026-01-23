package com.value.data.domain.dto;

import com.value.data.domain.entity.InterfaceUser;
import lombok.Data;

@Data
public class InterfaceUserDTO extends InterfaceUser {

    //记录用户信息

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机
     */
    private String mobile;

    /**
     * 组织id
     */
    private Integer deptId;

    /**
     * 用户昵称
     */
    private String nickName;

}
