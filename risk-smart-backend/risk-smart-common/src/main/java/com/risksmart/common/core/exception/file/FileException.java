package com.risksmart.common.core.exception.file;

import com.risksmart.common.core.exception.base.BaseException;

/**
 * 文件信息异常类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args, String msg)
    {
        super("file", code, args, msg);
    }

}
