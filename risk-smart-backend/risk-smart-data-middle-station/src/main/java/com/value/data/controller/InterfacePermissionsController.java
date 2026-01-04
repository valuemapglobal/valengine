package com.value.data.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.GetPermissionsDTO;
import com.value.data.domain.dto.InterfacePermissionsDTO;
import com.value.data.domain.entity.InterfacePermissionsManage;
import com.value.data.domain.entity.InterfaceUser;
import com.value.data.domain.vo.TreeInterfaceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/interfacePermissions")
@Slf4j(topic = "[InterfacePermissionsController]")
public class InterfacePermissionsController {

    private final ServiceConstant serviceConstant;

    @Autowired
    public InterfacePermissionsController(ServiceConstant serviceConstant){
        this.serviceConstant = serviceConstant;
    }


    /**
     * 获取菜单
     */
    @PostMapping("/treeMenuList")
    public AjaxResult TreeMenuList( HttpServletRequest request){
        LoginUser user = SecurityUtils.getLoginUser(request);
//        TreeNodeConfig
        List<TreeInterfaceVO> treeMenuList = serviceConstant.interfacePermissionsManageService
                .treeMenuList(user.getSysUser().getDeptId());

        return AjaxResult.success(treeMenuList);
    }

    /**
     * 获取授权列表
     */
    @PostMapping("/getPermissions")
    public AjaxResult getPermissions(@RequestBody @Valid GetPermissionsDTO getPermissionsDTO, HttpServletRequest request){
        SecurityUtils.getLoginUser(request);
        try {
            LambdaQueryWrapper<InterfacePermissionsManage> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(InterfacePermissionsManage::getUserId, getPermissionsDTO.getUserId());
            return AjaxResult.success(serviceConstant.interfacePermissionsManageService.list(lambdaQuery));
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }

    /**
     * 更新授权列表
     */
    @Transactional
    @PostMapping("/updatePermissions")
    public AjaxResult updatePermissions(@RequestBody InterfacePermissionsDTO interfacePermissionsDTO, HttpServletRequest request){
        SecurityUtils.getLoginUser(request);
        //设置回滚点,只回滚以下异常
        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {

            LambdaQueryWrapper<InterfaceUser> lambdaQueryUser = Wrappers.lambdaQuery();
            lambdaQueryUser.eq(InterfaceUser::getUserId, interfacePermissionsDTO.getUserId());
            long userCount = serviceConstant.interfaceUserService.count(lambdaQueryUser);
            if (userCount <= 0){return AjaxResult.error("未申请权限标识");}

            //移除全部数据
            LambdaQueryWrapper<InterfacePermissionsManage> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(InterfacePermissionsManage::getUserId, interfacePermissionsDTO.getUserId());
            serviceConstant.interfacePermissionsManageService.remove(lambdaQuery);

            //新增授权数据

            List<InterfacePermissionsManage> saveEntityList = new ArrayList<>();
            interfacePermissionsDTO.getManageNos().forEach(manageNo ->{
                InterfacePermissionsManage entity = new InterfacePermissionsManage();
                entity.setInterfaceManageNo(manageNo);
                entity.setUserId(interfacePermissionsDTO.getUserId());
                saveEntityList.add(entity);
            });

            serviceConstant.interfacePermissionsManageService.saveBatch(saveEntityList);
            return AjaxResult.success();
        }catch (Exception e){
            //手工回滚异常,回滚到savePoint
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }

}
