package com.value.decision.common.domain;

import java.io.Serializable;

/**
 * 通用响应类（泛型版本）
 * 用于微服务间通信、Feign调用等场景
 * 替代RuoYi框架的R类
 *
 * @param <T> 数据类型
 * @author OP-Lite Team
 * @since 1.0.0
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL = 500;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    public R() {
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @param <T> 数据类型
     * @return 成功消息
     */
    public static <T> R<T> ok() {
        return ok("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 成功消息
     */
    public static <T> R<T> ok(T data) {
        return ok("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param <T> 数据类型
     * @return 成功消息
     */
    public static <T> R<T> ok(String msg) {
        return ok(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 成功消息
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data);
    }

    /**
     * 返回失败消息
     *
     * @param <T> 数据类型
     * @return 失败消息
     */
    public static <T> R<T> fail() {
        return fail("操作失败");
    }

    /**
     * 返回失败消息
     *
     * @param msg 返回内容
     * @param <T> 数据类型
     * @return 失败消息
     */
    public static <T> R<T> fail(String msg) {
        return fail(msg, null);
    }

    /**
     * 返回失败消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 失败消息
     */
    public static <T> R<T> fail(String msg, T data) {
        return new R<>(FAIL, msg, data);
    }

    /**
     * 返回失败消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param <T>  数据类型
     * @return 失败消息
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 判断是否成功
     *
     * @return true成功 false失败
     */
    public boolean isSuccess() {
        return SUCCESS == code;
    }

    /**
     * 判断是否失败
     *
     * @return true失败 false成功
     */
    public boolean isFail() {
        return !isSuccess();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
