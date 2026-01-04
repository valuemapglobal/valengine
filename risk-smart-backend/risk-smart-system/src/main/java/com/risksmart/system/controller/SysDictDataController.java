package com.risksmart.system.controller;

import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysDictData;
import com.risksmart.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据 控制器
 * 前端调用: GET /vm/system/dict/data/type/{dictType}
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 获取字典数据列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return AjaxResult.success(list);
    }

    /**
     * 根据字典类型查询字典数据
     * 前端主要使用此接口获取下拉选项
     */
    @GetMapping("/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType) {
        List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
        return AjaxResult.success(data);
    }

    /**
     * 根据字典编码获取详细信息
     */
    @GetMapping("/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode) {
        return AjaxResult.success(dictDataService.getById(dictCode));
    }
}
