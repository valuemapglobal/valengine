package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.vo.CountLogVO;

/**
 * <p>
 * 接口日志表 服务类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceLogService extends IService<InterfaceLog> {

    CountLogVO countLog(Long deptId);
}
