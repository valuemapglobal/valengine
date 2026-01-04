package com.value.data.domain;

import com.value.data.common.constant.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VMHttpResult <T>{
    private Integer code;
    private String msg;
    private T data;

    public static VMHttpResult<Void> error() {
        return new VMHttpResult<>(HttpStatus.ERROR,"操作失败",null);
    }
    public static VMHttpResult<Void> error(String msg) {
        return new VMHttpResult<>(HttpStatus.ERROR,msg,null);
    }
    public static <T> VMHttpResult<T> error(String msg,T data) {
        return new VMHttpResult<>(HttpStatus.ERROR,msg,data);
    }
}
