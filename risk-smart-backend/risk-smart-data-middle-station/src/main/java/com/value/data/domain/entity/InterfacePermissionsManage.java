package com.value.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 接口权限管理表
 * </p>
 *
 * @author bing
 * @since 2023-10-27
 */
@Data
@TableName("interface_permissions_manage")
public class InterfacePermissionsManage implements Serializable {

    private static final long serialVersionUID=1L;
    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
