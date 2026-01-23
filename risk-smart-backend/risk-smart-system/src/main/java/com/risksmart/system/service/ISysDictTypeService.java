package com.risksmart.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.risksmart.system.domain.SysDictType;

import java.util.List;

/**
 * 字典类型 服务层
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 查询字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据字典类型查询
     */
    SysDictType selectDictTypeByType(String dictType);
}
