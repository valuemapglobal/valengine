package com.value.data.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterfaceFieldIdTypeEnum {
    IN_PARAM(0,"入参"),
    OUT_PARAM(1,"出参");
    private Integer code;
    private String msg;
}
