package com.risksmart.common.core.exception;

/**
 * 验证码错误异常类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class CaptchaException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException(String msg)
    {
        super(msg);
    }
}
