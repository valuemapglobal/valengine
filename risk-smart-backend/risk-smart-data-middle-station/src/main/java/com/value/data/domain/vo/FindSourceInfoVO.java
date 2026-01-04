package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取供应商信息
 */
@Data
public class FindSourceInfoVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 来源唯一标识
     */
    private String interfaceSourceNo;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 来源
     */
    private String source;

    /**
     * 网址
     */
    private String webLink;

    /**
     * 负责人
     */
    private String adminName;

    /**
     * 联系方式
     */
    private String contactDetails;

    /**
     * 0 元数据，1 特征变量，2 分析指标
     */
    private Integer interfaceDataType;
}
