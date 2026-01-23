package com.risksmart.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.risksmart.common.core.domain.R;
import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysLogininfor;
import com.risksmart.system.service.ISysLogininforService;

/**
 * 系统访问记录 控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController
{
    @Autowired
    private ISysLogininforService logininforService;

    /**
     * 获取登录日志列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysLogininfor logininfor)
    {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return AjaxResult.success(list);
    }

    /**
     * 删除登录日志
     */
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds)
    {
        return AjaxResult.success(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清空登录日志
     */
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        logininforService.cleanLogininfor();
        return AjaxResult.success();
    }

    /**
     * 新增登录日志（内部服务调用）
     */
    @PostMapping
    public R<Boolean> add(@RequestBody SysLogininfor logininfor)
    {
        return R.ok(logininforService.insertLogininfor(logininfor) > 0);
    }
}
