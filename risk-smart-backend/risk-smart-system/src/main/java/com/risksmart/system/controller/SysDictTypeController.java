package com.risksmart.system.controller;

import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysDictType;
import com.risksmart.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型 控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return AjaxResult.success(list);
    }

    /**
     * 根据字典类型查询
     */
    @GetMapping("/{dictType}")
    public AjaxResult getInfo(@PathVariable String dictType) {
        return AjaxResult.success(dictTypeService.selectDictTypeByType(dictType));
    }

    /**
     * 获取所有字典类型（供下拉选择）
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        List<SysDictType> list = dictTypeService.selectDictTypeList(new SysDictType());
        return AjaxResult.success(list);
    }
}
