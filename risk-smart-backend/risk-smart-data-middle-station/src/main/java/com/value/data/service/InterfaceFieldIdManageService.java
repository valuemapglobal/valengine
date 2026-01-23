package com.value.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import com.value.data.common.constant.InterfaceFieldIdTypeEnum;
import com.value.data.common.model.LoginUser;
import com.value.data.domain.entity.InterfaceFieldIdManage;

import java.util.List;

/**
 * <p>
 * 接口参数管理表 服务类
 * </p>
 *
 * @author bing
 * @since 2023-08-16
 */
public interface InterfaceFieldIdManageService extends IService<InterfaceFieldIdManage> {

    Boolean validInterfaceFieldIdNo(String fieldIdNo);

    Boolean validManageNo(String manageNo);

    Boolean validName(String manageNo,String interfaceFieldIdName,Integer interfaceFieldIdType,String parent,String fieldNo);
    Boolean validAlias(String manageNo, String interfaceFieldIdAlias, Integer interfaceFieldIdType,String parent,String fieldNo);

    String getInterfaceField(String interfaceFieldIdManage);

    List<InterfaceFieldIdManage> analyzeInterfaceField(String interfaceManageNo,
                                                     String interfaceNo,
                                                     InterfaceFieldIdTypeEnum fieldIdType,
                                                     JsonNode jsonNode,
                                                     LoginUser user);

    int saveFields(List<InterfaceFieldIdManage> fields);

    boolean checkInterfaceField(InterfaceFieldIdManage field);

    /**
     * 查询所有未同步到数据平台的接口参数
     */
    List<InterfaceFieldIdManage> listNotSync();
}
