package com.value.data.common.exception;

import com.value.data.common.constant.HttpStatus;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private Integer code;
    private String msg;
    private Object data;

    public ServiceException(Integer code, String msg, Throwable cause) {
        super(msg,cause);
        this.code = code;
        this.msg = msg;
    }
    public ServiceException(Integer code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ServiceException conflict(String msg){
        return new ServiceException(HttpStatus.CONFLICT,msg,null);
    }
    public static ServiceException conflict(String msg, Throwable cause){
        return new ServiceException(HttpStatus.CONFLICT,msg,cause);
    }
    public static ServiceException forbidden(String msg){
        return new ServiceException(HttpStatus.FORBIDDEN,msg,null);
    }
    public static ServiceException forbidden(String msg, Throwable cause){
        return new ServiceException(HttpStatus.FORBIDDEN,msg,cause);
    }
    public static ServiceException badRequest(String msg){
        return new ServiceException(HttpStatus.BAD_REQUEST,msg,null);
    }
    public static ServiceException badRequest(String msg, Throwable cause){
        return new ServiceException(HttpStatus.BAD_REQUEST,msg,cause);
    }
    public static ServiceException error(String msg){
        return new ServiceException(HttpStatus.ERROR,msg,null);
    }
    public static ServiceException error(String msg, Throwable cause){
        return new ServiceException(HttpStatus.ERROR,msg,cause);
    }
}
