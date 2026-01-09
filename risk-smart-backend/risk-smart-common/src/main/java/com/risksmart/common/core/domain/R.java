package com.risksmart.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应类
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    private int code;
    private String msg;
    private T data;

    public R() {
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    public static <T> R<T> ok() {
        return new R<>(SUCCESS, "操作成功", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, "操作成功", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data);
    }

    // ==================== 失败响应 ====================

    public static <T> R<T> fail() {
        return new R<>(FAIL, "操作失败", null);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL, msg, null);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(FAIL, msg, data);
    }

    // ==================== 状态判断 ====================

    public boolean isSuccess() {
        return SUCCESS == code;
    }

    public boolean isFail() {
        return !isSuccess();
    }

    public static <T> boolean isSuccess(R<T> r) {
        return r != null && r.isSuccess();
    }

    public static <T> boolean isError(R<T> r) {
        return !isSuccess(r);
    }
}
