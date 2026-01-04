package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.InterfaceManage;

import java.util.List;

/**
 * <p>
 * 接口管理表 服务类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceManageService extends IService<InterfaceManage> {

    Boolean validManageNo(String manageNo);

    InterfaceManage validInterfaceNo(String interfaceNo, String sourceNo);

    Boolean validSourceNo(String sourceNo);

    String getInterfaceNo(String interfaceManageNo);

    /**
     * 查询所有未同步到数据平台的接口接口
     */
    List<InterfaceManage> listNotSync();
}
