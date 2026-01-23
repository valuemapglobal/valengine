package com.value.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.data.domain.entity.InterfaceSourceManage;

import java.util.List;

/**
 * <p>
 * 接口供应商管理表 Mapper 接口
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceSourceManageMapper extends BaseMapper<InterfaceSourceManage> {
    /**
     * 查询所有未同步到数据平台的接口供应商
     */
    List<InterfaceSourceManage> listNotSync();
}
