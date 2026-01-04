package com.value.data.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.common.dto.EncryptDTO;
import com.value.data.common.utils.EncryptBodyUtil;
import com.value.data.common.service.RedisService;
import com.value.data.constant.IdConstant;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.vo.EncryptUserVO;
import com.value.data.service.InterfaceManageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author Vida
 * @date 2025年04月03日 11:38
 * @description
 */
@Slf4j
public class DataStationUtil {
    private final Map<String,Object> respMap = new HashMap<>();
    /**
     * 数据中台url
     */
    private final String url;
    /**
     * 请求参数，需要包含orderNo
     */
    private final Map<String,Object> params;
    private final InterfaceManageService service;
    @Getter
    private final EncryptUserVO user;

    public DataStationUtil(String url, Map<String,Object> params, InterfaceManageService service,RedisService redisService){
        this.url = url;
        this.params = params;
        this.service = service;

        final String orderNo = getOrderNo(params);
        params.put("orderNo",orderNo);
        params.put("orderId",orderNo);
        EncryptUserVO encryptUserVO = redisService.getCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(orderNo));
        Assert.isTrue(!Objects.isNull(encryptUserVO),"未找到用户信息");
        this.user = encryptUserVO;
    }

    public Map<String,Object> reqParams(){
        return params;
    }
    public Map<String,Object> respObj(String interfaceNo){
        final Object response = getInterfaceResponse(interfaceNo);
        return (Map<String,Object>)response;
    }

    public List<Object> respList(String interfaceNo){
        final Object response = getInterfaceResponse(interfaceNo);
        return (List<Object>)response;
    }

    private Object getInterfaceResponse(String interfaceNo){
        final Object response = respMap.get(interfaceNo);
        if (response!=null){ return response; }

        final String execute = execute(interfaceNo);
        if (execute==null || !JSONUtil.isJson(execute)){
            throw new RuntimeException(String.format("数据中台响应异常，接口代号%s，响应：%s",interfaceNo,execute));
        }
        final JSONObject jsonObject = JSONObject.parseObject(execute);
        final int code = jsonObject.getIntValue("code");
        if (code!=200){
            throw new RuntimeException(String.format("数据中台响应异常，接口代号%s，响应：%s",interfaceNo,execute));
        }
        final Object data = jsonObject.get("data");
        respMap.put(interfaceNo,data);
        return data;
    }
    private String execute(String interfaceNo){
        final InterfaceManage info = service.getOne(
                Wrappers.lambdaQuery(InterfaceManage.class).eq(InterfaceManage::getInterfaceNo, interfaceNo)
                        .eq(InterfaceManage::getDeptId,user.getDeptId())
        );
        final String orderNo = getOrderNo();
        final EncryptDTO reqBody;
        try {
            reqBody = EncryptBodyUtil.createEncryptBody(
                    "-",
                    "-",
                    info.getInterfaceManageNo(),
                    info.getInterfaceSourceNo(),
                    info.getInterfaceNo(),
                    orderNo,
                    params);
        } catch (Exception e) {
            log.error("请求加密异常",e);
            throw new RuntimeException("请求加密异常"+interfaceNo,e);
        }
        log.info("请求数据中台:orderNo={},interfaceNo={}",params.get("orderNo").toString(),interfaceNo);
        return HttpUtil.post(url, JSONObject.toJSONString(reqBody));
    }

    private String getOrderNo(){
        return getOrderNo(this.params);
    }
    public static String getOrderNo(Map<String,Object> params){
        final String orderNo = Optional.ofNullable(params.get("orderNo")).orElse("").toString();
        final String orderId = Optional.ofNullable(params.get("orderId")).orElse("").toString();
        return StrUtil.isBlank(orderId)?orderNo:orderId;
    }

}
