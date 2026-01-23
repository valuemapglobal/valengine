package com.value.data.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.data.common.model.LoginUser;
import com.value.data.domain.dto.SearchInterfaceSourceTreeDTO;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import com.value.data.mapper.InterfaceSourceManageMapper;
import com.value.data.service.InterfaceFieldIdManageService;
import com.value.data.service.InterfaceManageService;
import com.value.data.service.InterfaceSourceManageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 接口供应商管理表 服务实现类
 * </p>
 *
 * @author bing
 * @since 2023-08-14
 */
@Service
@AllArgsConstructor
public class InterfaceSourceManageServiceImpl extends ServiceImpl<InterfaceSourceManageMapper, InterfaceSourceManage>
        implements InterfaceSourceManageService {
    private final InterfaceManageService interfaceService;
    private final InterfaceFieldIdManageService fieldService;

    @Override
    public Boolean validSourceNo(String sourceNo) {
        LambdaQueryWrapper<InterfaceSourceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceSourceManage::getInterfaceSourceNo, sourceNo);
        long count = count(lambdaQuery);
        return count > 0;
    }

    @Override
    public IPage<InterfaceSourceManage> pageByTree(SearchInterfaceSourceTreeDTO dto, LoginUser user) {
        //查询数据
        final Page<InterfaceSourceManage> page = page(new Page<>(dto.getPageNum(), dto.getPageSize()),
                Wrappers.lambdaQuery(dto.getSource()).eq(InterfaceSourceManage::getDeptId,user.getSysUser().getDeptId())
        );
        final List<InterfaceSourceManage> sources = page.getRecords();
        final Set<String> sourceNos = sources.stream().map(InterfaceSourceManage::getInterfaceSourceNo)
                .collect(Collectors.toSet());

        Optional.ofNullable(dto.getInterfaceManage()).ifPresent(i -> i.setInterfaceSourceNo(null));
        final List<InterfaceManage> interfaceManages = interfaceService.list(
                Wrappers.lambdaQuery(dto.getInterfaceManage()).in(InterfaceManage::getInterfaceSourceNo, sourceNos)
        );

        final Set<String> manageNos = interfaceManages.stream().map(InterfaceManage::getInterfaceManageNo)
                .collect(Collectors.toSet());
        final List<InterfaceFieldIdManage> fields = fieldService.list(
                Wrappers.lambdaQuery(dto.getField()).in(InterfaceFieldIdManage::getInterfaceManageNo, manageNos)
        );

        //处理关系
        final Map<String, List<InterfaceFieldIdManage>> fieldsMap = fields.stream()
                .collect(Collectors.groupingBy(InterfaceFieldIdManage::getInterfaceManageNo));
        interfaceManages.stream().forEach(i -> i.setFields(fieldsMap.get(i.getInterfaceManageNo())));

        final Map<String, List<InterfaceManage>> interfaceManagesMap = interfaceManages.stream()
                .collect(Collectors.groupingBy(InterfaceManage::getInterfaceSourceNo));
        sources.stream().forEach(s -> s.setInterfaceManageList(interfaceManagesMap.get(s.getInterfaceSourceNo())));

        return page;
    }

    @Override
    public List<InterfaceSourceManage> listNotSync() {
        return baseMapper.listNotSync();
    }

    @Override
    public List<InterfaceSourceManage> quotaInterfaceTree(LoginUser user) {
        //查询供应商数据
        final List<InterfaceSourceManage> sources = this.lambdaQuery()
                .eq(InterfaceSourceManage::getDeptId,user.getSysUser().getDeptId())
                .eq(InterfaceSourceManage::getInterfaceDataType,2)
                .select(InterfaceSourceManage::getId,InterfaceSourceManage::getInterfaceSourceNo,InterfaceSourceManage::getDataName)
                .list();
        if (CollUtil.isEmpty(sources)){
            return new ArrayList<>();
        }
        //查询接口数据
        final Set<String> sourceNos = sources.stream().map(InterfaceSourceManage::getInterfaceSourceNo)
                .collect(Collectors.toSet());
        final List<InterfaceManage> interfaceManages = interfaceService.lambdaQuery()
                .in(InterfaceManage::getInterfaceSourceNo,sourceNos)
                .select(
                        InterfaceManage::getId,InterfaceManage::getInterfaceSourceNo, InterfaceManage::getInterfaceManageNo,
                        InterfaceManage::getInterfaceNo,InterfaceManage::getInterfaceName
                )
                .list();
        if (CollUtil.isEmpty(interfaceManages)){
            return sources;
        }
        //查询属性字段
        final Set<String> manageNos = interfaceManages.stream().map(InterfaceManage::getInterfaceManageNo)
                .collect(Collectors.toSet());
        final List<InterfaceFieldIdManage> fields = fieldService.lambdaQuery()
                .in(InterfaceFieldIdManage::getInterfaceManageNo,manageNos)
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType,1)
                .list();

        //处理关系
        final Map<String, List<InterfaceFieldIdManage>> fieldsMap = fields.stream()
                .collect(Collectors.groupingBy(InterfaceFieldIdManage::getInterfaceManageNo));
        interfaceManages.stream().forEach(i -> i.setFields(fieldsMap.get(i.getInterfaceManageNo())));

        final Map<String, List<InterfaceManage>> interfaceManagesMap = interfaceManages.stream()
                .collect(Collectors.groupingBy(InterfaceManage::getInterfaceSourceNo));
        sources.stream().forEach(s -> s.setInterfaceManageList(interfaceManagesMap.get(s.getInterfaceSourceNo())));

        return sources;
    }
}
