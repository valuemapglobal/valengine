package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.system.domain.SysDictType;
import com.risksmart.system.mapper.SysDictTypeMapper;
import com.risksmart.system.service.ISysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典类型 服务实现
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();

        if (dictType != null) {
            if (StringUtils.hasText(dictType.getDictName())) {
                wrapper.like(SysDictType::getDictName, dictType.getDictName());
            }
            if (StringUtils.hasText(dictType.getDictType())) {
                wrapper.like(SysDictType::getDictType, dictType.getDictType());
            }
            if (StringUtils.hasText(dictType.getStatus())) {
                wrapper.eq(SysDictType::getStatus, dictType.getStatus());
            }
        }

        wrapper.orderByAsc(SysDictType::getDictId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        return baseMapper.selectOne(wrapper);
    }
}
