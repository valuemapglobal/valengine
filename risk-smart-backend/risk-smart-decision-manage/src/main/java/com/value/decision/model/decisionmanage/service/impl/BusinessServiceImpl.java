package com.value.decision.model.decisionmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.decision.model.decisionmanage.mapper.BusinessMapper;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.model.decisionmanage.service.BusinessService;
import com.value.decision.common.security.LoginUser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业务场景表服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {
    @Override
    public List<Business> queryList(Business business, LoginUser loginUser) {
        final LambdaQueryWrapper<Business> queryWrapper = Wrappers.lambdaQuery(business);
        queryWrapper.eq(Business::getDeptId,loginUser.getSysUser().getDeptId().intValue())
        .or().eq(Business::getDeptId,1);
        return list(queryWrapper);
    }

    @Override
    public List<Business> standardList() {
        LambdaQueryWrapper<Business> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Business::getDeptFlag,1)
                .eq(Business::getDataStatus,0)
                .orderByDesc(Business::getCreateTime);
        return this.list(wrapper);
    }

}
