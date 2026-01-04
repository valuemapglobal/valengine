package com.value.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.data.domain.entity.InterfacePermissionsManage;
import com.value.data.domain.vo.TreeInterfaceVO;
import com.value.data.mapper.InterfacePermissionsManageMapper;
import com.value.data.service.InterfacePermissionsManageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 接口权限管理表 服务实现类
 * </p>
 *
 * @author bing
 * @since 2023-10-27
 */
@Service
public class InterfacePermissionsManageServiceImpl extends ServiceImpl<InterfacePermissionsManageMapper, InterfacePermissionsManage> implements InterfacePermissionsManageService {

    @Override
    public List<TreeInterfaceVO> treeMenuList(Long deptId) {
        return baseMapper.treeMenuList(deptId);
    }
}
