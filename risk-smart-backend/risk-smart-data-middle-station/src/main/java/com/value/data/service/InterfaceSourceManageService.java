package com.value.data.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.common.model.LoginUser;
import com.value.data.domain.dto.SearchInterfaceSourceTreeDTO;
import com.value.data.domain.entity.InterfaceSourceManage;

import java.util.List;

/**
 * <p>
 * 接口供应商管理表 服务类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
public interface InterfaceSourceManageService extends IService<InterfaceSourceManage> {

    Boolean validSourceNo(String sourceNo);

    IPage<InterfaceSourceManage> pageByTree(SearchInterfaceSourceTreeDTO dto, LoginUser user);

    /**
     * 查询所有未同步到数据平台的接口供应商
     */
    List<InterfaceSourceManage> listNotSync();

    List<InterfaceSourceManage> quotaInterfaceTree(LoginUser user);
}
