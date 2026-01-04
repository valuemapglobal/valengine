package com.value.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.vo.CountLogVO;

/**
 * <p>
 * 接口日志表 Mapper 接口
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceLogMapper extends BaseMapper<InterfaceLog> {

    CountLogVO countLog(Long deptId);
}
