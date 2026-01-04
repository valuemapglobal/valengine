package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.MetricsAttributeEntity;

/**
 * <p>
 * 分析指标属性表 服务类
 * </p>
 *
 * @author Vida
 * @since 2025-04-23
 */
public interface MetricsAttributeService extends IService<MetricsAttributeEntity> {

    /**
     * 检查名称是否已存在
     *
     * @param name 名称
     * @return 是否存在
     */
    boolean isNameExists(Long moduleId,String name);

    /**
     * 检查编码是否已存在
     *
     * @param code 编码
     * @return 是否存在
     */
    boolean isCodeExists(Long moduleId,String code);

    /**
     * 检查名称是否已存在（排除指定ID）
     *
     * @param name 名称
     * @param id   排除的ID
     * @return 是否存在
     */
    boolean isNameExistsExcludingId(Long moduleId,String name, Long id);

    /**
     * 检查编码是否已存在（排除指定ID）
     *
     * @param code 编码
     * @param id   排除的ID
     * @return 是否存在
     */
    boolean isCodeExistsExcludingId(Long moduleId,String code, Long id);

    /**
     * 检查名称或编码是否已存在
     *
     * @param name 名称
     * @param code 编码
     * @return 是否存在
     */
    boolean isNameOrCodeExists(Long moduleId,String name, String code);

    /**
     * 检查名称或编码是否已存在（排除当前记录）
     *
     * @param name 名称
     * @param code 编码
     * @param id 当前记录ID
     * @return 是否存在
     */
    boolean isNameOrCodeExistsExcludingId(Long moduleId,String name, String code, Long id);

    /**
     * 预执行公式，检查公式是否正确
     * @param attributeEntity
     * @return
     */
    boolean checkFormula(MetricsAttributeEntity attributeEntity);

    /**
     * 检查属性是否属于指定部门
     * @param id 属性id
     * @param deptId 部门id
     * @return true:是 false:否
     */
    boolean checkAttributeDept(Long id,Long deptId);

}