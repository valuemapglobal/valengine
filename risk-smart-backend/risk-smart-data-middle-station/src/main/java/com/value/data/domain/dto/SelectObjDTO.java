package com.value.data.domain.dto;

import lombok.Data;

/**
 * 选择的元数据或特征变量对象数据传输对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class SelectObjDTO {
    /**
     * 关联的元数据或特征变量的对象类型 5
     */
    private String objectFlag;
    /**
     * 关联元数据或特征变量 名称
     */
    private String objectName;
    /**
     * 关联元数据或特征变量 别名
     */
    private String anotherName;
    /**
     * 关联元数据或特征变量选中的属性名称
     */
    private String stats;
    /**
     * 关联元数据或特征变量选中的属性数据类型
     */
    private String dataType;
}
