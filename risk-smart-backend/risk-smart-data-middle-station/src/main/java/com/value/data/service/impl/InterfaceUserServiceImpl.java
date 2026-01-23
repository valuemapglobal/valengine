package com.value.data.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.risksmart.common.core.domain.R;
import com.risksmart.system.api.RemoteUserService;
import com.value.data.domain.dto.InterfaceUserDTO;
import com.value.data.domain.entity.InterfaceUser;
import com.value.data.mapper.InterfaceUserMapper;
import com.value.data.service.InterfaceUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author luke
 * @since 2023-10-27
 */
@Service
public class InterfaceUserServiceImpl extends ServiceImpl<InterfaceUserMapper, InterfaceUser> implements InterfaceUserService {
    @Autowired
    private InterfaceUserMapper interfaceUserMapper;
    @Autowired
    @Lazy
    private RemoteUserService remoteUserService;

    @Override
    public InterfaceUserDTO queryUserInfoByAppKey(String appKey) {
        LambdaQueryWrapper<InterfaceUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceUser::getAppKey, appKey);
        InterfaceUser interfaceUser = interfaceUserMapper.selectOne(wrapper);
        if(interfaceUser!=null){
            InterfaceUserDTO interfaceUserDTO = new InterfaceUserDTO();
            BeanUtils.copyProperties(interfaceUser,interfaceUserDTO);
            R getUserByIdR = remoteUserService.queryUserById(interfaceUser.getUserId());
            if (getUserByIdR.getData()!=null){
                Map<String,Object> map = (HashMap<String, Object>) getUserByIdR.getData();
                if(map.get("userName") !=null){
                    interfaceUserDTO.setUserName(map.get("userName").toString());
                }
                if(map.get("phonenumber") !=null){
                    interfaceUserDTO.setMobile(map.get("phonenumber").toString());
                }
                if(map.get("userId")!=null){
                    interfaceUserDTO.setUserId((Integer) map.get("userId"));
                }
                if(map.get("deptId")!=null){
                    interfaceUserDTO.setDeptId((Integer)map.get("deptId"));
                }
                if(map.get("nickName")!=null){
                    interfaceUserDTO.setNickName(map.get("nickName").toString());
                }
            }
            return interfaceUserDTO;

        }
        return null;
    }
}
