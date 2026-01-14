package com.value.data.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.risksmart.common.core.constant.ValidatedType;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.domain.dto.InterfaceDeptAppDTO;
import com.value.data.domain.entity.InterfaceDeptApp;
import com.value.data.domain.vo.InterfaceDeptAppVO;
import com.value.data.service.InterfaceDeptAppService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Vida
 * @since 2023-12-06
 */
@RestController
@RequestMapping("/interfaceDeptApp")
public class InterfaceDeptAppController {
    @Resource
    private InterfaceDeptAppService interfaceDeptAppService;

    /**
     * 添加
     */
    @PostMapping("/saveDeptApp")
    public AjaxResult saveDeptApp(@RequestBody @Validated(ValidatedType.add.class) InterfaceDeptAppDTO param, HttpServletRequest request){
        //登陆验证
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!Objects.equals(user.getSysUser().getDeptId(), param.getDeptId())){
            return AjaxResult.forbidden("无权操作其它部门");
        }
        //参数封装
        InterfaceDeptApp interfaceDeptApp = new InterfaceDeptApp();
        BeanUtil.copyProperties(param,interfaceDeptApp);
        //判断部门是否已存在
        LambdaQueryWrapper<InterfaceDeptApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceDeptApp::getDeptId,interfaceDeptApp.getDeptId());
        long count = interfaceDeptAppService.count(wrapper);
        if (count>0){return AjaxResult.conflict("部门id已存在");}
        //保存
        interfaceDeptAppService.save(interfaceDeptApp);
        return AjaxResult.success();
    }
    /**
     * 删除
     */
    @PostMapping("/deleteDeptApp")
    public AjaxResult deleteDeptApp(@RequestBody @Validated(ValidatedType.delete.class) InterfaceDeptAppDTO param, HttpServletRequest request){
        //登陆验证
        LoginUser user = SecurityUtils.getLoginUser(request);
        //删除
        interfaceDeptAppService.remove(Wrappers.lambdaQuery(InterfaceDeptApp.class)
                .eq(InterfaceDeptApp::getDeptId,user.getSysUser().getDeptId())
                .eq(InterfaceDeptApp::getId,param.getId())
        );
        return AjaxResult.success();
    }
    /**
     * 修改
     */
    @PostMapping("/updateDeptApp")
    public AjaxResult updateDeptApp(@RequestBody @Validated(ValidatedType.update.class) InterfaceDeptAppDTO param, HttpServletRequest request){
        //登陆验证
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!Objects.equals(user.getSysUser().getDeptId(), param.getDeptId())){
            return AjaxResult.forbidden("无权操作其它部门");
        }
        //参数封装
        InterfaceDeptApp interfaceDeptApp = new InterfaceDeptApp();
        BeanUtil.copyProperties(param,interfaceDeptApp);
        //保存
        interfaceDeptAppService.updateById(interfaceDeptApp);
        return AjaxResult.success();
    }
    /**
     * 查询
     */
    @PostMapping("/queryDeptAppPage")
    public AjaxResult queryDeptAppPage(@RequestBody @Validated(ValidatedType.query.class) InterfaceDeptAppDTO param, HttpServletRequest request){
        //登陆验证
        LoginUser user = SecurityUtils.getLoginUser(request);
        //分页查询
        InterfaceDeptApp interfaceDeptApp = new InterfaceDeptApp();
        BeanUtil.copyProperties(param,interfaceDeptApp);
        LambdaQueryWrapper<InterfaceDeptApp> wrapper = new LambdaQueryWrapper<>(interfaceDeptApp);
        wrapper.eq(InterfaceDeptApp::getDeptId,user.getSysUser().getDeptId());
        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        List<InterfaceDeptApp> list = interfaceDeptAppService.list(wrapper);
        PageInfo<InterfaceDeptApp> pageInfo = new PageInfo<>(list);
        if (CollUtil.isEmpty(list)) {return AjaxResult.success(pageInfo);}
        //转成vo，进行参数控制
        List<InterfaceDeptAppVO> voList = list.stream().map(p -> {
            InterfaceDeptAppVO vo = new InterfaceDeptAppVO();
            BeanUtil.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());
        PageInfo<InterfaceDeptAppVO> page = new PageInfo<>();
        BeanUtil.copyProperties(pageInfo,page);
        page.setList(voList);
        return AjaxResult.success(page);
    }


    /**
     * 提交
     */
    @PostMapping("/submit")
    public AjaxResult submitDeptApp(@RequestBody @Validated(ValidatedType.add.class) InterfaceDeptAppDTO param, HttpServletRequest request){
        //登陆验证
        LoginUser user = SecurityUtils.getLoginUser(request);
        if (!Objects.equals(user.getSysUser().getDeptId(), param.getDeptId())){
            return AjaxResult.forbidden("无权操作其它部门");
        }
        //参数封装
        InterfaceDeptApp interfaceDeptApp = new InterfaceDeptApp();
        BeanUtil.copyProperties(param,interfaceDeptApp);

        LambdaQueryWrapper<InterfaceDeptApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceDeptApp::getDeptId,interfaceDeptApp.getDeptId());
        InterfaceDeptApp one = interfaceDeptAppService.getOne(wrapper);
        if (param.getId()==null){//添加
            if (one!=null){return AjaxResult.conflict("部门id已存在");}
            interfaceDeptAppService.save(interfaceDeptApp);
        }else {//修改
            if (one!=null && !Objects.equals(one.getId(), param.getId())){return AjaxResult.badRequest("id不匹配");}
            interfaceDeptAppService.updateById(interfaceDeptApp);
        }
        return AjaxResult.success();
    }

}
