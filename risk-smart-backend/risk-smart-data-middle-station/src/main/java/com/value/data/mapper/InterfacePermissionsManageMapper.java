package com.value.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.value.data.domain.entity.InterfacePermissionsManage;
import com.value.data.domain.vo.TreeInterfaceVO;

import java.util.List;

/**
 * <p>
 * 接口权限管理表 Mapper 接口
 * </p>
 *
 * @author bing
 * @since 2023-10-27
 */
public interface InterfacePermissionsManageMapper extends BaseMapper<InterfacePermissionsManage> {

    List<TreeInterfaceVO> treeMenuList(Long deptId);
}
