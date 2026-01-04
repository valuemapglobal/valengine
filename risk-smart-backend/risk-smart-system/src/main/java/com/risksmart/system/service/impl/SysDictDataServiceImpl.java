package com.risksmart.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.system.domain.SysDictData;
import com.risksmart.system.mapper.SysDictDataMapper;
import com.risksmart.system.service.ISysDictDataService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典数据 服务实现
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        return baseMapper.selectDictDataByType(dictType);
    }

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();

        if (dictData != null) {
            if (StringUtils.hasText(dictData.getDictType())) {
                wrapper.eq(SysDictData::getDictType, dictData.getDictType());
            }
            if (StringUtils.hasText(dictData.getDictLabel())) {
                wrapper.like(SysDictData::getDictLabel, dictData.getDictLabel());
            }
            if (StringUtils.hasText(dictData.getStatus())) {
                wrapper.eq(SysDictData::getStatus, dictData.getStatus());
            }
        }

        wrapper.orderByAsc(SysDictData::getDictSort);
        return baseMapper.selectList(wrapper);
    }
}
