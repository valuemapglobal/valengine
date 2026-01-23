package com.value.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.mapper.InterfaceManageMapper;
import com.value.data.service.InterfaceManageService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 接口管理表 服务实现类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Service
public class InterfaceManageServiceImpl extends ServiceImpl<InterfaceManageMapper, InterfaceManage> implements InterfaceManageService {

    @Override
    public Boolean validManageNo(String manageNo) {
        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getInterfaceManageNo, manageNo);
        long count = count(lambdaQuery);
        return count > 0;
    }

    @Override
    public InterfaceManage validInterfaceNo(String interfaceNo, String sourceNo) {
        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getInterfaceNo, interfaceNo)
                .eq(InterfaceManage::getInterfaceSourceNo, sourceNo);
        return getOne(lambdaQuery);
    }

    @Override
    public Boolean validSourceNo(String sourceNo) {
        LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceManage::getInterfaceSourceNo, sourceNo);
        long count = count(lambdaQuery);
        return count > 0;
    }

    @Override
    public String getInterfaceNo(String interfaceManageNo) {
        final InterfaceManage interfaceManage = this.getOne(
                Wrappers.lambdaQuery(InterfaceManage.class).eq(InterfaceManage::getInterfaceManageNo, interfaceManageNo)
        );
        return Objects.isNull(interfaceManage)?null:interfaceManage.getInterfaceNo();
    }

    @Override
    public List<InterfaceManage> listNotSync() {
        return baseMapper.listNotSync();
    }
}
