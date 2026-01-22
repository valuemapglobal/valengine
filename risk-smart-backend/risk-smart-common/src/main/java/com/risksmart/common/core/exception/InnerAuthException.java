package com.risksmart.common.core.exception;

/**
 * 内部认证异常
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class InnerAuthException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public InnerAuthException(String message)
    {
        super(message);
    }
}
