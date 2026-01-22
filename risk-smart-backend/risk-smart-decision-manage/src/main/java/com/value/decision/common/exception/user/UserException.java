package com.value.decision.common.exception.user;

import com.value.decision.common.exception.BaseException;

/**
 * 用户信息异常类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
