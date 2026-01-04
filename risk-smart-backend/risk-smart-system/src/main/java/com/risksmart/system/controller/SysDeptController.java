package com.risksmart.system.controller;

import com.risksmart.common.core.web.AjaxResult;
import com.risksmart.system.domain.SysDept;
import com.risksmart.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门管理 控制器
 * 前端调用: GET /vm/system/dept/list
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public AjaxResult list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return AjaxResult.success(depts);
    }

    /**
     * 根据部门ID获取详细信息
     */
    @GetMapping("/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId) {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }
}
