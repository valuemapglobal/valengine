package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.value.data.domain.dto.InterfaceUserDTO;
import com.value.data.domain.entity.InterfaceUser;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author luke
 * @since 2023-10-27
 */
public interface InterfaceUserService extends IService<InterfaceUser> {

    InterfaceUserDTO queryUserInfoByAppKey(String appKey);

}
