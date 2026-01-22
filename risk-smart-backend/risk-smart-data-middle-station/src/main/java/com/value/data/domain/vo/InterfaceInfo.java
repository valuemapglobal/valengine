package com.value.data.domain.vo;

import lombok.Data;

/**
 * 接口信息视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class InterfaceInfo {
    //供应商
    private String sourceNo;
    //接口序号
    private String manageNo;
    //接口编号
    private String interfaceNo;
    //接口类型
    private Integer interfaceType;
    //入参
    private String paramKey = "paramData";

    /**
     * 返回值类型（0-对象，5-数组）
     */
    private Integer returnType;
}
