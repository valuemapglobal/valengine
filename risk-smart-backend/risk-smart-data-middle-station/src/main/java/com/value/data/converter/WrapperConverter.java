package com.value.data.converter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.risksmart.common.core.utils.StringUtils;
import com.value.data.domain.dto.FindInterfaceFieldIdInfoDTO;
import com.value.data.domain.dto.FindSourceInfoDTO;
import com.value.data.domain.dto.InterfaceQueryDTO;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;

import java.util.List;

/**
 * @author Vida
 * @date 2025年03月19日 13:20
 * @description
 */
public class WrapperConverter {
    public static Wrapper<InterfaceManage> convert(InterfaceQueryDTO dto ){
        final LambdaQueryWrapper<InterfaceManage> wrapper = Wrappers.lambdaQuery(InterfaceManage.class);
        wrapper.eq(InterfaceManage::getDeptId,dto.getDeptId())
                .in(
                        CollectionUtil.isNotEmpty(dto.getInterfaceNos()),
                        InterfaceManage::getInterfaceNo,dto.getInterfaceNos()
                );
        return wrapper;
    }

    public static LambdaQueryWrapper<InterfaceSourceManage> convert(FindSourceInfoDTO findSourceInfoDTO ){
        LambdaQueryWrapper<InterfaceSourceManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceSourceManage::getDataStatus, 0);
        //供应商名称模糊搜索
        if (StringUtils.isNotEmpty(findSourceInfoDTO.getSourceName())){
            lambdaQuery.like(InterfaceSourceManage::getDataName,findSourceInfoDTO.getSourceName());
        }
        //筛选数据类型
        if (CollectionUtil.isNotEmpty(findSourceInfoDTO.getInterfaceDataType())){
            final List<Integer> interfaceDataType = findSourceInfoDTO.getInterfaceDataType();
            if (interfaceDataType.size()==1){
                lambdaQuery.eq(InterfaceSourceManage::getInterfaceDataType,interfaceDataType.get(0));
            }else {
                lambdaQuery.in(InterfaceSourceManage::getInterfaceDataType,findSourceInfoDTO.getInterfaceDataType());
            }
        }
        //根据创建时间倒叙排列
        lambdaQuery.orderByDesc(InterfaceSourceManage::getCreateTime);
        return lambdaQuery;
    }

    public static LambdaQueryWrapper<InterfaceFieldIdManage> convert(FindInterfaceFieldIdInfoDTO findInterfaceFieldIdInfoDTO){
        LambdaQueryWrapper<InterfaceFieldIdManage> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(InterfaceFieldIdManage::getDataStatus, 0)
                .eq(InterfaceFieldIdManage::getInterfaceManageNo, findInterfaceFieldIdInfoDTO.getManageNo());
        if (findInterfaceFieldIdInfoDTO.getInterfaceFieldIdType()!=null){
            lambdaQuery.eq(InterfaceFieldIdManage::getInterfaceFieldIdType, findInterfaceFieldIdInfoDTO.getInterfaceFieldIdType());
        }
        if (StrUtil.isNotBlank(findInterfaceFieldIdInfoDTO.getInterfaceFieldIdName())){
            lambdaQuery.last("and interface_fie_id_name like '%".concat(findInterfaceFieldIdInfoDTO.getInterfaceFieldIdName()).concat("%'"));
        }
        return lambdaQuery;
    }
}
