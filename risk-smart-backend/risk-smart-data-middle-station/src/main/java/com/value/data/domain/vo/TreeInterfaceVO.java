package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TreeInterfaceVO implements Serializable {

    /**
     * 场景 id
     */
    private Long sourceId;

    /**
     * 数据场景 标识
     */
    private String interfaceSourceNo;

    /**
     * 数据场景 名称
     */
    private String dataName;

    /**
     * 子集
     */
    private List<TreeInterfaceChildrenVO> children;
}
