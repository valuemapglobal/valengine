package com.value.data.controller;

import com.alibaba.fastjson2.JSONObject;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.DataStationUtil;
import com.value.data.service.FeatureVariableService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Vida
 * @date 2025年04月03日 11:25
 * @description
 */
@RequestMapping("/feature")
@RestController
@Slf4j
@AllArgsConstructor
public class FeatureVariableController {
    private final FeatureVariableService service;
    /**
     * 特征变量入口函数，必须通过数据中台调用，因为需要数据中台提供的orderNo
     * @param params 请求参数
     * @param name 特征变量模块名称
     * @return 计算处理后的衍生变量
     */
    @PostMapping("/entry-point/{name}")
    public AjaxResult entryPoint(@RequestBody Map<String,Object> params,@PathVariable String name){
        log.info("-----开始执行特征变量{}，orderNo={}-----",name, DataStationUtil.getOrderNo(params));
        final Object result = service.entryPoint(params, name);
        log.info("-----{}特征变量执行完毕，结果：{}-----",name, JSONObject.toJSONString(result));
        return AjaxResult.success(result);
    }
}
