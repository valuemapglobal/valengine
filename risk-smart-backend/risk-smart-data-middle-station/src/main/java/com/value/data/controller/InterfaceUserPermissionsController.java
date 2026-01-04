package com.value.data.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteUserService;
import com.value.data.common.utils.AppUtils;
import com.value.data.domain.dto.AppKeyAndSecretDTO;
import com.value.data.domain.dto.InterfaceUserDTO;
import com.value.data.domain.entity.InterfaceUser;
import com.value.data.service.InterfaceUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/interfaceUserPermissions")
@Slf4j(topic = "[InterfaceUserPermissionsController]")
public class InterfaceUserPermissionsController {

    @Autowired
    private InterfaceUserService interfaceUserService;
    @Resource
    private RemoteUserService remoteUserService;

    /**
     * 申请appkey and secret
     */
    @GetMapping("generateAppKeyAndSecret")
    public R generateAppKeyAndSecret(){
        String appKey = AppUtils.getAppId();
        String appSecret = AppUtils.getAppSecret(appKey);
        AppKeyAndSecretDTO appKeyAndSecretDTO = new AppKeyAndSecretDTO();
        appKeyAndSecretDTO.setAppKey(appKey);
        appKeyAndSecretDTO.setSecret(appSecret);
        return R.ok(appKeyAndSecretDTO);
    }

    /**
     * 申请接口用户
     */
    @PostMapping("addInterfaceUser")
    public R addInterfaceUser(@RequestBody InterfaceUserDTO interfaceUserDTO){
        LambdaQueryWrapper<InterfaceUser> interfaceUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        interfaceUserLambdaQueryWrapper.eq(InterfaceUser::getUserId,interfaceUserDTO.getUserId());
        InterfaceUser one = interfaceUserService.getOne(interfaceUserLambdaQueryWrapper);
        if (one!=null){
            return R.fail("该用户已申请appkey");
        }
        InterfaceUser interfaceUser = new InterfaceUser();
        BeanUtils.copyProperties(interfaceUserDTO,interfaceUser);
        interfaceUser.setCreateTime(LocalDateTime.now());
        interfaceUserService.save(interfaceUser);
        return R.ok("接口用户申请成功");
    }

    @GetMapping("/queryAppkeyByUserId")
    public R<InterfaceUser> queryAppKeyByUserId(@RequestParam Integer userId){
        LambdaQueryWrapper<InterfaceUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceUser::getUserId,userId);
        InterfaceUser interfaceUser = interfaceUserService.getOne(wrapper);
        if(interfaceUser==null){
            return R.fail("改用户没有对应的接口权限信息");
        }
        return R.ok(interfaceUser);
    }

    /**
     * 根据appkey查询对应的信息
     */
    @GetMapping("queryByAppKey")
    public R queryByAppKey(@RequestParam("appKey")String appKey) {
        InterfaceUserDTO interfaceUserDTO = interfaceUserService.queryUserInfoByAppKey(appKey);
        if(interfaceUserDTO==null){
            return R.fail("根据appKey未查询到对应的接口用户信息");
        }
        return R.ok(interfaceUserDTO);
    }
}
