package com.value.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.vo.CountLogVO;
import com.value.data.mapper.InterfaceLogMapper;
import com.value.data.service.InterfaceLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 接口日志表 服务实现类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Service
public class InterfaceLogServiceImpl extends ServiceImpl<InterfaceLogMapper, InterfaceLog> implements InterfaceLogService {

    @Override
    public CountLogVO countLog(Long deptId) {
        return baseMapper.countLog(deptId);
    }
}
