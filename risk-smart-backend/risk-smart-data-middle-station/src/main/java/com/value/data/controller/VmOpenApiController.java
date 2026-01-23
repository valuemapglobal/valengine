package com.value.data.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.risksmart.common.core.domain.EncryptDTO;
import com.risksmart.common.core.exception.ServiceException;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.utils.sign.EncryptBodyUtil;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.EncryptTool;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.EncryptParamNameConstant;
import com.value.data.constant.IdConstant;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.entity.InterfaceDeptApp;
import com.value.data.domain.vo.EncryptUserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/openApi")
@Slf4j(topic = "[OpenApiController]")
@AllArgsConstructor
public class VmOpenApiController {
    private VmOpenApiProperties properties;
    private ServiceConstant service;
    private Environment env;

    @Component
    @RefreshScope
    @Data
    @ConfigurationProperties(prefix = "vm-open-api")
    public static class VmOpenApiProperties{
        private String authCode ;
        private String authCodeName ;
        private String companyBaseInfoSourceNo;
        private String companyBaseInfoInterfaceManageNo;
        private String companyBaseInfoInterfaceNo;
        private List<String> inputParamCompanyName;
        private List<String> outputParamCompanyName;
    }

    /**
     * 调用vm开放平台通用方法
     * @param apiName 接口代号
     * @param returnName 返回值参数名（data体内，可以为空，为空返回整个data体）
     * @param param 实际业务参数，从数据中台传递过来需要包含orderId或orderNo参数
     * @param request http请求
     * @return 接口响应
     */
    @PostMapping(value = {"/valueMapApi/{apiName}/{returnName}","/valueMapApi/{apiName}"})
    public AjaxResult valueMapApiTool(@PathVariable String apiName,
                                      @PathVariable(required = false) String returnName,
                                      @RequestBody(required = false) JSONObject param,
                                      HttpServletRequest request
    ){
        if (StrUtil.isBlank(apiName)){
            return AjaxResult.error("apiName is blank");
        }
        param.put(properties.getAuthCodeName(),properties.getAuthCode());
        Long deptId = null;String order = null;
        //拿到orderId
        String orderId = param.getString(EncryptParamNameConstant.ORDER_ID);
        String orderNo = param.getString(EncryptParamNameConstant.ORDER_NO);
        if (StringUtils.isEmpty(orderId) && StringUtils.isEmpty(orderNo)){
            LoginUser loginUser = SecurityUtils.getLoginUser(request);
            deptId = loginUser.getSysUser().getDeptId();
            order = IdUtil.simpleUUID();
        }else {
            //获取账户信息
            order = StringUtils.isEmpty(orderId)?orderNo:orderId;
            EncryptUserVO encryptVO = service.redisService.getCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(order));
            //获取部门id
            if (encryptVO==null || encryptVO.getDeptId()==null){
                return AjaxResult.error("获取deptId异常,请检查订单号");
            }
            deptId = encryptVO.getDeptId().longValue();
        }
        //根据部门查估图账号
        LambdaQueryWrapper<InterfaceDeptApp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceDeptApp::getDeptId,deptId);
        InterfaceDeptApp one = service.interfaceDeptAppService.getOne(wrapper);
        //判空
        if (one==null || StringUtils.isEmpty(one.getAppKey()) || StringUtils.isEmpty(one.getSecret()) || StringUtils.isEmpty(one.getUrl())){
            return AjaxResult.error("部门账户信息异常，请检查估图账号配置");
        }

        JSONObject body = sendVm(order, apiName, one, param);
        return AjaxResult.success(getVmData(returnName,body));
    }

    /**
     * 请求估图开放平台
     * @param order 订单号，用于定位请求
     * @param apiName 开放平台接口代号
     * @param one 开放平台账号信息、url
     * @param param 实际业务参数
     * @return 请求成功响应体
     */
    private JSONObject sendVm(String order,String apiName,InterfaceDeptApp one,JSONObject param){
        if (StringUtils.isEmpty(order)){
            throw new ServiceException("订单号为空");
        }
        String dto = EncryptTool.createEncryptBody(apiName, one.getAppKey(), one.getSecret(), param, order);
        log.info("请求vm开放平台，url:{},params:{}",one.getUrl(),dto);
        String re = HttpUtil.post(one.getUrl(), dto);
        log.info("接口返回值：{}",re);
        //处理响应
        if (!JSONUtil.isJsonObj(re)){throw new ServiceException("服务通讯异常，稍后重试");}
        JSONObject body = JSONObject.parseObject(re);
        if (body.get("code") == null){throw new ServiceException("服务通讯异常，稍后重试2");}
        if (body.getIntValue("code") != 0){throw new ServiceException(body.getString("msg"));}
        return body;
    }

    /**
     * 解析估图开放平台响应
     * @param returnName data体内的返回参数名
     * @param body 完整响应体
     * @return 根据returnName去响应中取data内的返回参数，如果returnName为空，则返回整个data
     */
    private Object getVmData(String returnName,JSONObject body){
        if (StrUtil.isBlank(returnName)){
            return body.get("data");
        }
        JSONObject data = body.getJSONObject("data");
        return data.get(returnName);
    }

    @PostMapping(value = {"/valueMapApi2/{apiName}/{returnName}","/valueMapApi2/{apiName}"})
    public AjaxResult valueMapApiTool2(@PathVariable String apiName,
                                      @PathVariable(required = false) String returnName,
                                      @RequestBody(required = false) JSONObject param,
                                      HttpServletRequest request
    ){
        //填充企业工商信息ckey
        try {
            populateCkey(param);
        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
       return valueMapApiTool(apiName,returnName,param,request);
    }
    private void populateCkey(JSONObject param) throws Exception {
        Optional<String> optional = properties.getInputParamCompanyName().stream()
                .filter(n -> StrUtil.isNotBlank(param.getString(n)))
                .map(param::getString)
                .distinct()
                .findFirst();
        if (!optional.isPresent()){
            return;
        }
        String companyName = optional.get();
        properties.getOutputParamCompanyName().stream().forEach(n -> param.put(n,companyName));


        EncryptDTO encryptBody = EncryptBodyUtil.createEncryptBody(
                "-",
                "-",
                properties.getCompanyBaseInfoInterfaceManageNo(),
                properties.getCompanyBaseInfoSourceNo(),
                properties.getCompanyBaseInfoInterfaceNo(),
                StrUtil.isBlank(param.getString("orderId")) ? param.getString("orderNo") : param.getString("orderId"),
                param
        );
        final String url = "http://127.0.0.1:"+env.getProperty("server.port")+"/interfaceRequest/api";
        String result = HttpUtil.post(url, JSONObject.toJSONString(encryptBody));
        if (!JSONUtil.isJsonObj(result)){
            throw new ServiceException("获取ckey失败01："+result);
        }
        JSONObject jsonResult = JSONObject.parseObject(result);
        JSONObject data = jsonResult.getJSONObject("data");
        if (data==null){
            throw new ServiceException("获取ckey失败02：无企业基本信息数据");
        }
        String ckey = data.getString("ckey");
        if (StrUtil.isBlank(ckey)){
            throw new ServiceException("获取ckey失败03：未找到ckey数");
        }
        param.put("ckey",ckey);
    }
}
