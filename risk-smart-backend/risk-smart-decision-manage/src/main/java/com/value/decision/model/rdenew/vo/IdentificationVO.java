package com.value.decision.model.rdenew.vo;

import lombok.Data;

/**
 * 对象名称加类型标识
 */

@Data
public class IdentificationVO {

    /**
     * 对象和其类型标识
     */
    private String objectFlag;

    /**
     * 对象名称 / 模块名称
     */
    private String objectName;

    /**
     * 模块别名
     */
    private String anotherName;

    /**
     * 属性名称
     */
    private String stats;

    /**
     * 数据类型 1元数据 2特征变量 3分析指标
     */
    private String dataType;

    /**
     * 如果为分析指标则则会有此drl字段,并拼在此规则的后面
     */
    private String drlCode = "";

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

}
