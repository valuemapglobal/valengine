package com.value.data.domain.vo;

import lombok.Data;

/**
 * @author Vida
 * @date 2024年01月30日 16:37
 * @description
 */
@Data
public class InputParameterVO {
    /**
     * 参数名称
     */
    private String interfaceFieldIdName;

    /**
     * 参数别名
     */
    private String interfaceFieldIdAlias;

    /**
     * 数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件，6-布尔，7-小数)
     */
    private Integer interfaceFieldIdDataType;
}
