package com.value.decision.model.decisionmanage.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.constant.OperationType;
import com.value.decision.common.constant.SecurityConstants;
import com.value.decision.framework.aspectj.lang.annotation.RequirLoginUser;
import com.value.decision.model.decisionmanage.model.Business;
import com.value.decision.model.decisionmanage.model.Product;
import com.value.decision.model.decisionmanage.model.dto.BusinessQueryDTO;
import com.value.decision.model.decisionmanage.service.BusinessService;
import com.value.decision.model.decisionmanage.service.RuoYiService;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 业务场景表 前端控制器
 * </p>
 *
 * @author Vida
 * @since 2024-11-07
 */
@RestController
@RequestMapping("/business")
public class BusinessController {
    @Resource
    private BusinessService service;

    @Resource
    private RuoYiService ruoYiService;

    @PostMapping
    @RequirLoginUser
    public AjaxResult save(@Validated(OperationType.Create.class) @RequestBody Business business,LoginUser loginUser) {
        business.setUserId(loginUser.getUserid());
        business.setDeptId(loginUser.getSysUser().getDeptId());
        business.setDeptFlag((short) (ruoYiService.getStandardDepts().contains(loginUser.getSysUser().getDeptId())?1:2));
        return AjaxResult.success(service.save(business));
    }

    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable Long id){
        return AjaxResult.success(service.removeById(id));
    }

    @PutMapping
    public AjaxResult update(@Validated(OperationType.Update.class) @RequestBody Business business) {
        return AjaxResult.success(service.updateById(business));
    }

    @PostMapping("/_search")
    public AjaxResult query(@RequestBody(required = false) BusinessQueryDTO business) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null){
            return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
        }
        if (business != null && (business.getPageNum()!=null && business.getPageSize()!=null)){
            PageHelper.startPage(business.getPageNum(),business.getPageSize());
        }
        final List<Business> list = service.queryList(business,loginUser);
        final PageInfo<Business> pageInfo = new PageInfo<>(list);
        return AjaxResult.success(pageInfo);
    }

    @GetMapping("/_standard")
    @RequirLoginUser
    public AjaxResult standardList() {
        List<Business> list = service.standardList();
        return AjaxResult.success(list);
    }

}

