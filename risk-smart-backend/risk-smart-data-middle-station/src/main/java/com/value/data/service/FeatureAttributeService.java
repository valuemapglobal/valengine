package com.value.data.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.FeatureAttributeEntity;

/**
 * 特征变量属性表 服务类
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface FeatureAttributeService extends IService<FeatureAttributeEntity> {
    IPage<FeatureAttributeEntity> page(IPage<FeatureAttributeEntity> page, String name);

    /**
     * 检查名称是否已存在
     *
     * @param moduleId 模块ID
     * @param name     名称
     * @return 是否存在
     */
    boolean isNameExists(Long moduleId, String name);

    /**
     * 检查编码是否已存在
     *
     * @param moduleId 模块ID
     * @param code     编码
     * @return 是否存在
     */
    boolean isCodeExists(Long moduleId, String code);

    /**
     * 检查名称是否已存在（排除指定ID）
     *
     * @param moduleId 模块ID
     * @param name     名称
     * @param id       排除的ID
     * @return 是否存在
     */
    boolean isNameExistsExcludingId(Long moduleId, String name, Long id);

    /**
     * 检查编码是否已存在（排除指定ID）
     *
     * @param moduleId 模块ID
     * @param code     编码
     * @param id       排除的ID
     * @return 是否存在
     */
    boolean isCodeExistsExcludingId(Long moduleId, String code, Long id);

    /**
     * 检查是否存在相同名称或编码的特征变量属性
     *
     * @param moduleId 模块ID
     * @param name 名称
     * @param code 编码
     * @return 是否存在
     */
    boolean isNameOrCodeExists(Long moduleId, String name, String code);

    /**
     * 检查是否存在相同名称或编码的特征变量属性（排除当前记录）
     *
     * @param moduleId 模块ID
     * @param id 当前记录的ID
     * @param name 名称
     * @param code 编码
     * @return 是否存在
     */
    boolean isNameOrCodeExistsExcludingId(Long moduleId, Long id, String name, String code);

    /**
     * 检查指定数据是否属于指定部门
     * @param id 数据id
     * @param deptId 部门id
     * @return true-数据属于该部门，false-数据不属于该部门
     */
    boolean checkDept(Long id, Long deptId);
}