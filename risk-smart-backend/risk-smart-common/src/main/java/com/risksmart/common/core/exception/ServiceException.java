package com.risksmart.common.core.exception;

import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 附加数据
     */
    private final Object data;

    public ServiceException(String message) {
        this(message, null, null, null);
    }

    public ServiceException(String message, Integer code) {
        this(message, code, null, null);
    }

    public ServiceException(String message, Integer code, Object data) {
        this(message, code, data, null);
    }

    public ServiceException(String message, Integer code, Throwable cause) {
        this(message, code, null, cause);
    }

    public ServiceException(String message, Integer code, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    // ==================== 静态工厂方法 ====================

    public static ServiceException of(String message) {
        return new ServiceException(message);
    }

    public static ServiceException of(String message, Integer code) {
        return new ServiceException(message, code);
    }

    public static ServiceException badRequest(String message) {
        return new ServiceException(message, 400);
    }

    public static ServiceException unauthorized(String message) {
        return new ServiceException(message, 401);
    }

    public static ServiceException forbidden(String message) {
        return new ServiceException(message, 403);
    }

    public static ServiceException notFound(String message) {
        return new ServiceException(message, 404);
    }

    public static ServiceException conflict(String message) {
        return new ServiceException(message, 409);
    }

    public static ServiceException error(String message) {
        return new ServiceException(message, 500);
    }

    public static ServiceException error(String message, Throwable cause) {
        return new ServiceException(message, 500, cause);
    }
}