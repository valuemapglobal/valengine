package com.value.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.data.domain.entity.InterfaceFieldIdManage;

import java.util.List;

/**
 * <p>
 * 接口参数管理表 Mapper 接口
 * </p>
 *
 * @author bing
 * @since 2023-08-16
 */
public interface InterfaceFieldIdManageMapper extends BaseMapper<InterfaceFieldIdManage> {
    /**
     * 查询所有未同步到数据平台的接口参数
     */
    List<InterfaceFieldIdManage> listNotSync();
}
