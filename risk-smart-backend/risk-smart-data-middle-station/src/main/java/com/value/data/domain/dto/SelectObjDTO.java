package com.value.data.domain.dto;

import lombok.Data;

/**
 * @author Vida
 * @date 2023年08月22日 13:56
 * @description 选择的元数据或特征变量对象
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
