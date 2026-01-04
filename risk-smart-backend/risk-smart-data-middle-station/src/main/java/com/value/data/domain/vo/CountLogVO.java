package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据报表
 */
@Data
public class CountLogVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 累计调用量
     */
    private Integer total;

    /**
     * 近七日调用平均响应时间
     */
    private Double avgResponseTime7days;

    /**
     * 昨日调用量
     */
    private Integer callsYesterday;

    /**
     * 近七日调用成功量
     */
    private Integer callsSuccess7days;

    /**
     * 近七日调用失败量
     */
    private Integer callsFailure7days;

    /**
     * 成功百分比
     */
    private Double successPercentage;

    /**
     * 成功的数量
     */
    private Integer successCount;

    /**
     * 失败百分比
     */
    private Double failurePercentage;

    /**
     * 失败的数量
     */
    private Integer failureCount;

    /**
     * 接口总量
     */
    private Integer interfaceTotal;
}
