package com.value.data.domain.vo;

import lombok.Data;

/**
 * 字段树形结构视图对象
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class FieldTree extends TreeList{
    private Integer fieldType;
}
