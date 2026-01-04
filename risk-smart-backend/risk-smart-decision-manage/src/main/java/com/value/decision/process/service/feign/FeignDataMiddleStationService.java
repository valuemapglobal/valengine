package com.value.decision.process.service.feign;

import com.alibaba.fastjson2.JSONObject;
import com.value.decision.process.dto.FindInterfaceInfoDTO;
import com.value.decision.common.dto.EncryptDTO;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.process.dto.GetInterfaceInputParameterDTO;
import com.value.decision.process.vo.InterfaceUser;
import com.value.decision.common.domain.R;
import com.value.decision.process.dto.FindInterfaceFieldIdInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Austin
 * Create by 2025/7/3 10:58
 */
@FeignClient(contextId = "feignDataMiddleStationService", value = "risk-smart-data-middle-station" , path = "/interfaceManage")
public interface FeignDataMiddleStationService {

    /**
     * 通过Feign调用中台获取接口字段信息
     */
    @PostMapping("/findInterfaceFieldIdInfo")
    AjaxResult findInterfaceFieldIdInfo(@RequestBody FindInterfaceFieldIdInfoDTO dto);

    /**
     * 通过Feign调用中台获取接口信息
     */
    @PostMapping("/findInterfaceInfo")
    AjaxResult findInterfaceInfo(@RequestBody FindInterfaceInfoDTO dto);

    /**
     * 根据接口的标识列表获取所有入参的并集并根据英文名去重
     */
    @PostMapping("/interfaceManage/getInterfaceInputParameter")
    public AjaxResult getInterfaceInputParameter(@RequestBody GetInterfaceInputParameterDTO params);


    /** 根据用户id查询appkey */
    @GetMapping("/interfaceUserPermissions/queryAppkeyByUserId")
    public R<InterfaceUser> queryAppKeyByUserId(@RequestParam(value = "userId") Integer userId);

    @PostMapping("/interfaceRequest/api")
    JSONObject api(@RequestBody EncryptDTO encryptBody);
}
