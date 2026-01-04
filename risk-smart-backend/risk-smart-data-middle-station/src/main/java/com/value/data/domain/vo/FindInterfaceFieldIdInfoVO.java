package com.value.data.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 获取接口参数信息
 */
@Data
public class FindInterfaceFieldIdInfoVO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 接口参数唯一标识
     */
    private String interfaceFieldIdManage;

    /**
     * 接口管理唯一标识
     */
    private String interfaceManageNo;

    /**
     * 接口编号
     */
    private String interfaceNo;

    /**
     * 参数名称
     */
    private String interfaceFieldIdName;

    /**
     * 参数别名
     */
    private String interfaceFieldIdAlias;

    /**
     * 参数说明
     */
    private String interfaceFieldIdDescription;

    /**
     * 参数类型(0-入参，1-出参)
     */
    private Integer interfaceFieldIdType;

    /**
     * 数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件)
     */
    private Integer interfaceFieldIdDataType;

    /**
     * 入参必填项(0-是，1-否)
     */
    private Integer interfaceFieldIdRequired;

    /**
     * 接口序号
     */
    private Integer interfaceFieldIdIndex;

    /**
     * 参数备注
     */
    private String interfaceFieldIdRemark;

    /**
     * 默认值
     */
    private String interfaceFieldIdDefultValue;

    /**
     * 父级属性
     */
    private String interfaceFieldIdFather;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
