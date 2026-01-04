package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TreeInterfaceChildrenVO implements Serializable {
    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 接口编号
     */
    private String interfaceNo;

    /**
     * 接口名称
     */
    private String interfaceName;
}
