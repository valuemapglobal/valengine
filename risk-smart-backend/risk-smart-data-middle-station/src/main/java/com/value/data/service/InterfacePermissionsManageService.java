package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.InterfacePermissionsManage;
import com.value.data.domain.vo.TreeInterfaceVO;

import java.util.List;

/**
 * <p>
 * 接口权限管理表 服务类
 * </p>
 *
 * @author bing
 * @since 2023-10-27
 */
public interface InterfacePermissionsManageService extends IService<InterfacePermissionsManage> {

    List<TreeInterfaceVO> treeMenuList(Long deptId);
}
