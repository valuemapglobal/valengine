package com.risksmart.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.risksmart.common.core.constant.SecurityConstants;
import com.risksmart.system.api.RemoteLogService;
import com.risksmart.system.api.domain.SysOperLog;

/**
 * 异步调用日志服务
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
public class AsyncLogService
{
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog) throws Exception {
        if (sysOperLog == null)
        {
            return;
        }
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
