package com.value.decision.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.risksmart.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 数据层
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
