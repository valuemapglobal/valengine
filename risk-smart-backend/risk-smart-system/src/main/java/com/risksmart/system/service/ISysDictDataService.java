package com.risksmart.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.risksmart.system.domain.SysDictData;

import java.util.List;

/**
 * 字典数据 服务层
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 查询字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);
}
