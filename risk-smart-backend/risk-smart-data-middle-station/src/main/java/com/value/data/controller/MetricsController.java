package com.value.data.controller;

import com.alibaba.fastjson2.JSONObject;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.DataStationUtil;
import com.value.data.service.MetricsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Vida
 * @date 2025年04月24日 13:34
 * @description
 */
@RestController
@RequestMapping("/metrics")
@Slf4j
@AllArgsConstructor
public class MetricsController {
    private final MetricsService service;

    /**
     * 分析指标入口函数
     * @param params
     * @param name
     * @return
     */
    @PostMapping("/entry-point/{name}")
    public AjaxResult entryPoint(@RequestBody Map<String,Object> params, @PathVariable String name){
        log.info("-----开始执行分析指标{}，orderNo={}-----",name, DataStationUtil.getOrderNo(params));
        final Object result = service.entryPoint(params, name);
        log.info("-----{}分析指标执行完毕，结果：{}-----",name, JSONObject.toJSONString(result));
        return AjaxResult.success(result);
    }
}
