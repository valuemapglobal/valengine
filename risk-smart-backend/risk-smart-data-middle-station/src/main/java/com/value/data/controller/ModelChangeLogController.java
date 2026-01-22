package com.value.data.controller;

import com.value.data.common.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 模型变更日志控制器
 * 用于记录模型轨迹日志（非关键功能）
 */
@RestController
@RequestMapping("/modelChangeLog")
@Slf4j
public class ModelChangeLogController {

    /**
     * 添加模型变更日志
     * @param data 日志数据
     * @return 结果
     */
    @PostMapping("/addModelChangeLog")
    public AjaxResult addModelChangeLog(@RequestBody Map<String, Object> data) {
        log.info("模型变更日志: {}", data);
        // 目前仅记录日志，后续可扩展为存储到数据库
        return AjaxResult.success("日志记录成功");
    }
}
