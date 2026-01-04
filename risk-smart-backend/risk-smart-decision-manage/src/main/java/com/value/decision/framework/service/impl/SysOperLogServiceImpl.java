package com.value.decision.framework.service.impl;

import com.risksmart.system.domain.SysOperLog;
import com.value.decision.framework.mapper.SysOperLogMapper;
import com.value.decision.framework.service.ISysOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 操作日志 服务层处理
 */
@Slf4j
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLog.setOperTime(new Date());
        try {
            operLogMapper.insert(operLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}
