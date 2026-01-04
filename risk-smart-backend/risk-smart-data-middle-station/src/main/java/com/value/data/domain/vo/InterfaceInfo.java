package com.value.data.domain.vo;

import lombok.Data;

/**
 * @author Vida
 * @date 2023年08月21日 11:38
 * @description
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
