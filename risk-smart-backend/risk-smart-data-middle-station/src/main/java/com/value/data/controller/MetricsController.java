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
 * 分析指标控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
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
