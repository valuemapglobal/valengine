package com.value.data.controller;

import com.risksmart.common.core.utils.StringUtils;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.DeleteGroupDTO;
import com.value.data.domain.dto.RdeRiskVariableGroupDTO;
import com.value.data.domain.vo.PageData;
import com.value.data.domain.vo.RdeRiskVariableGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 元数据分组
 */
@RestController
@Slf4j
@RequestMapping("/metadata/group")
public class RdeRiskVariableGroupController {
    private ServiceConstant service;

    public RdeRiskVariableGroupController(ServiceConstant service) {
        this.service = service;
    }

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public AjaxResult list(@RequestBody RdeRiskVariableGroupDTO record, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        PageData list = null;
        try {
            list = service.rdeRiskVariableGroupService.list(record, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(list);
    }

    /**
     * 根据ID查询一个
     */
    @GetMapping(value = "/{groupNo}")
    public AjaxResult getInfo(@PathVariable("groupNo") String groupNo) {
        if (StringUtils.isEmpty(groupNo))
            return AjaxResult.success("groupNo不能为空");

        RdeRiskVariableGroupVO rdeRiskVariableGroupVO = null;
        try {
            rdeRiskVariableGroupVO = service.rdeRiskVariableGroupService.select(groupNo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(rdeRiskVariableGroupVO);
    }

    /**
     * 新增
     */
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody @Validated RdeRiskVariableGroupDTO record, HttpServletRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
        int submit = 0;
        try {
            submit = service.rdeRiskVariableGroupService.submit(record, loginUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(submit);
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    public AjaxResult update(@RequestBody @Validated RdeRiskVariableGroupDTO record, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (StringUtils.isEmpty(record.getGroupNo()) && StringUtils.isEmpty(record.getInterfaceManageNo()))
            return AjaxResult.success("groupNo和interfaceManageNo不能都为空");

        int submit = 0;
        try {
            submit = service.rdeRiskVariableGroupService.update(record, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(submit);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public AjaxResult delete(@RequestBody DeleteGroupDTO record, HttpServletRequest request) {
        LoginUser user = SecurityUtils.getLoginUser(request);
        int delete = 0;
        try {
            delete = service.rdeRiskVariableGroupService.delete(record, user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
        return AjaxResult.success(delete);
    }

    @PostMapping("/isExisted")
    public AjaxResult isExisted(@RequestBody RdeRiskVariableGroupDTO record) {
        try {
            boolean existed = service.rdeRiskVariableGroupService.isExisted(record);
            return AjaxResult.success(existed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return AjaxResult.error("服务异常");
        }
    }
}
