package com.risksmart.system.controller;

import com.risksmart.common.core.domain.R;
import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysDictData;
import com.risksmart.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 内部服务调用 - 根据字典类型查询字典数据
     * Feign 接口调用: GET /dict/data/type/{dictType}
     */
    @GetMapping("/inner/type/{dictType}")
    public R<List<com.risksmart.system.api.domain.SysDictData>> innerDictType(@PathVariable String dictType) {
        List<SysDictData> data = dictDataService.selectDictDataByType(dictType);
        // 转换为 API 类型
        List<com.risksmart.system.api.domain.SysDictData> result = data.stream()
                .map(d -> {
                    com.risksmart.system.api.domain.SysDictData apiData = new com.risksmart.system.api.domain.SysDictData();
                    apiData.setDictCode(d.getDictCode());
                    apiData.setDictSort(Long.valueOf(d.getDictSort()));
                    apiData.setDictLabel(d.getDictLabel());
                    apiData.setDictValue(d.getDictValue());
                    apiData.setDictType(d.getDictType());
                    apiData.setCssClass(d.getCssClass());
                    apiData.setListClass(d.getListClass());
                    apiData.setIsDefault(d.getIsDefault());
                    apiData.setStatus(d.getStatus());
                    return apiData;
                })
                .collect(Collectors.toList());
        return R.ok(result);
    }
}
