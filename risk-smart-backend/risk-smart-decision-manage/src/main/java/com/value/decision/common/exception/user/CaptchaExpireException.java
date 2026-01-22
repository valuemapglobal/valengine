package com.value.decision.common.exception.user;

/**
 * 验证码失效异常类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class CaptchaExpireException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException()
    {
        super("user.jcaptcha.expire", null);
    }
}
