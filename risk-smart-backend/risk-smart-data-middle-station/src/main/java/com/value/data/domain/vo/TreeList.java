package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 树形列表
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Data
public class TreeList {
    private String no;
    private String name;
    private String code;
    private List<InterfaceInfo> interfaceInfos;
    private List<TreeList> chridren;
}
