package com.value.data.service;

import com.value.data.common.model.LoginUser;
import com.value.data.domain.dto.*;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.entity.InterfaceSourceManage;
import com.value.data.domain.vo.InterfaceChargingVo;

/**
 * @author Vida
 * @date 2023年08月16日 19:48
 * @description
 */
public interface InterfaceService {

    /**
     * 接口计费
     */
    InterfaceChargingVo interfaceCharging(InterfaceChargingDTO interfaceChargingDTO);

    /**
     * 保存供应商并创建主题
     */
    void saveSourceInfo(InterfaceSourceManage interfaceSourceManage, LoginUser user);
    /**
     * 删除供应商并删除主题
     */
    void removeSourceInfo(RemoveSourceInfo removeSourceInfo, LoginUser user);
    /**
     * 修改供应商并修改主题
     */
    void updateSourceInfo(UpdateSourceInfoDTO updateSourceInfoDTO, LoginUser user);




    /**
     * 保存接口信息，并新建对象
     */
    int saveInterfaceInfo(InterfaceManage interfaceManage, LoginUser user);

    /**
     * 删除接口
     */
    int removeInterfaceInfo(RemoveInterfaceInfoDTO removeInterfaceInfoDTO,LoginUser user);

    /**
     * 修改接口
     */
    int updateInterfaceInfo(UpdateInterfaceInfo updateInterfaceInfo, LoginUser user);





    /**
     * 保存参数
     */
    int saveInterfaceFieldIdInfo(SaveInterfaceFieldIdInfoDTO saveInterfaceFieldIdInfoDTO, LoginUser user) throws Exception;
    int saveInterfaceFieldIdInfo(InterfaceFieldIdManage interfaceFieldIdInfo, LoginUser user);

    /**
     * 删除参数
     */
    int removeInterfaceFieldIdInfo(RemoveInterfaceFieldIdInfoDTO removeInterfaceFieldIdInfoDTO,LoginUser user);

    /**
     * 修改参数
     */
    int updateInterfaceFieldIdInfo(UpdateInterfaceFieldIdInfoDTO updateInterfaceFieldIdInfoDTO, LoginUser user) throws Exception;
}
