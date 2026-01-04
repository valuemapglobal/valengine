package com.value.decision.common.exception;

/**
 * @ClassName LoginAuthException
 * @Date 2022-05-16 14:35:00
 * @Author Jane
 * @Description
 */
public class LoginAuthException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public LoginAuthException(String message)
    {
        super(message);
    }
}
