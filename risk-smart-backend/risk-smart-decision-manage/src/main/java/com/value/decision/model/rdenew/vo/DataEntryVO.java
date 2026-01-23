package com.value.decision.model.rdenew.vo;

import lombok.Data;

@Data
public class DataEntryVO {

    /**
     * 接口信息唯一标识
     */
    private String manageNo;

    /**
     * 接口供应商唯一标识
     */
    private String sourceNo;

    /**
     * 接口编号
     */
    private String interfaceNo;

    /**
     * 对象或集合标识  0为对象 5为集合
     */
    private String objectFlag;

    /**
     * 对象模块/名称
     */
    private String objectName;

}
