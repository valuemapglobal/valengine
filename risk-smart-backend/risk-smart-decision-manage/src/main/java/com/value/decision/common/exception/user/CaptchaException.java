package com.value.decision.common.exception.user;

/**
 * 验证码错误异常类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class CaptchaException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException()
    {
        super("user.jcaptcha.error", null);
    }
}
