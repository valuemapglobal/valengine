package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.entity.MetricsModuleEntity;

/**
 * <p>
 * 分析指标模块表 服务类
 * </p>
 *
 * @author Vida
 * @since 2025-04-23
 */
public interface MetricsModuleService extends IService<MetricsModuleEntity> {
    /**
     * 检查名称或编码是否已存在
     */
    boolean isNameOrCodeExists(String name, String code,Long moduleId,Long deptId);
    default boolean isNameExists(String name,Long moduleId,Long deptId){
        return isNameOrCodeExists(name,null,moduleId,deptId);
    }
    default boolean isCodeExists(String code,Long moduleId,Long deptId){
        return isNameOrCodeExists(null,code,moduleId,deptId);
    }

    /**
     * 更新模块的关联元数据/特征变量信息
     * 根据属性选择的字段，自动跟新模块的关联信息
     * @param moduleId 模块id
     * @return 是否成功更新
     */
    boolean updateModuleAssociation(Long moduleId);

    /**
     * 检查模块是否与部门关联
     * @param moduleId 模块id
     * @param deptId 部门id
     * @return true-关联，false-未关联
     */
    boolean checkModuleDept(Long moduleId,Long deptId);
}
