package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.FeatureModuleEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 特征变量模块表 服务类
 * </p>
 *
 * @author Vida
 * @since 2025-04-15
 */
public interface FeatureModuleService extends IService<FeatureModuleEntity> {
    /**
     * 根据ID删除特征变量模块
     * @param id 模块ID
     */
    @Transactional
    void deleteFeatureModule(Long id);

    /**
     * 检查名称或编码是否已存在
     * @param name 名称
     * @param code 编码
     * @param moduleId 模块Id，非必填，用于更新时排除当前记录
     * @param deptId 部门Id，用于数据隔离下的排除
     * @return 存在返回true，否则返回false
     */
    boolean isNameOrCodeExists(String name, String code,Long moduleId,Long deptId);
    default boolean isNameExists(String name,Long moduleId,Long deptId){
        return isNameOrCodeExists(name,null,moduleId,deptId);
    }
    default boolean isCodeExists(String code,Long moduleId,Long deptId){
        return isNameOrCodeExists(null,code,moduleId,deptId);
    }


    /**
     * 更新模块的关联元数据信息
     * 根据属性选择的字段，自动跟新模块的关联元数据信息
     * @param moduleId 模块id
     * @return 是否成功更新
     */
    boolean updateModuleMetadata(Long moduleId);

    /**
     * 检查指定模块是否属于指定的部门
     * @param moduleId 模块标识
     * @param deptId 部门id
     * @return true: 模块属于该部门，false: 模块不属于该部门
     */
    boolean checkModuleDept(Long moduleId,Long deptId);
}