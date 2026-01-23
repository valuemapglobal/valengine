package com.value.decision.common.exception.user;

/**
 *
 * @Author: Elijah
 * @DateTime: 2022/7/25 16:11
 */
public class UserPasswordLoginErrorException extends UserException{

    private static final long serialVersionUID = 1L;

    public UserPasswordLoginErrorException(String code,Object[] args)
    {
        super(code, args);
    }

}
