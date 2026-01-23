package com.value.data.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.value.data.domain.entity.FeatureAttributeEntity;
import com.value.data.mapper.FeatureAttributeMapper;
import com.value.data.service.FeatureAttributeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 特征变量属性表 服务实现类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Service
@AllArgsConstructor
@Slf4j
public class FeatureAttributeServiceImpl
        extends ServiceImpl<FeatureAttributeMapper, FeatureAttributeEntity>
        implements FeatureAttributeService {

    @Override
    public IPage<FeatureAttributeEntity> page(IPage<FeatureAttributeEntity> page, String name) {
        return baseMapper.selectPage(page,
                Wrappers.<FeatureAttributeEntity>lambdaQuery().like(StringUtils.isNotBlank(name), FeatureAttributeEntity::getName, name)
        );
    }

    @Override
    public boolean isNameExists(Long moduleId, String name) {
        return this.lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .eq(FeatureAttributeEntity::getName, name)
                .count() > 0;
    }

    @Override
    public boolean isCodeExists(Long moduleId, String code) {
        return this.lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .eq(FeatureAttributeEntity::getCode, code)
                .count() > 0;
    }

    @Override
    public boolean isNameExistsExcludingId(Long moduleId, String name, Long id) {
        return this.lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .eq(FeatureAttributeEntity::getName, name)
                .ne(FeatureAttributeEntity::getId, id)
                .count() > 0;
    }

    @Override
    public boolean isCodeExistsExcludingId(Long moduleId, String code, Long id) {
        return this.lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .eq(FeatureAttributeEntity::getCode, code)
                .ne(FeatureAttributeEntity::getId, id)
                .count() > 0;
    }

    @Override
    public boolean isNameOrCodeExists(Long moduleId, String name, String code) {
        final Long count = baseMapper.selectCount(Wrappers.<FeatureAttributeEntity>lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .and(wrapper -> wrapper
                        .eq(FeatureAttributeEntity::getName, name).or().eq(FeatureAttributeEntity::getCode, code))
                );
        return count > 0;
    }

    @Override
    public boolean isNameOrCodeExistsExcludingId(Long moduleId, Long id, String name, String code) {
        final Long count = baseMapper.selectCount(Wrappers.<FeatureAttributeEntity>lambdaQuery()
                .eq(FeatureAttributeEntity::getModuleId, moduleId)
                .and(wrapper -> wrapper
                        .eq(FeatureAttributeEntity::getName, name).or().eq(FeatureAttributeEntity::getCode, code)
                )
                .ne(FeatureAttributeEntity::getId, id));
        return count > 0;
    }

    @Override
    public boolean checkDept(Long id, Long deptId) {
        return this.lambdaQuery()
                .eq(FeatureAttributeEntity::getId,id)
                .eq(FeatureAttributeEntity::getDeptId,deptId)
                .count() > 0;
    }
}
