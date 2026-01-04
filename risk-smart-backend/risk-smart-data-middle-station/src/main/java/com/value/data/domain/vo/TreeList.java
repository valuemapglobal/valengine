package com.value.data.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Vida
 * @date 2023年08月18日 17:16
 * @description
 */
@Data
public class TreeList {
    private String no;
    private String name;
    private String code;
    private List<InterfaceInfo> interfaceInfos;
    private List<TreeList> chridren;
}
