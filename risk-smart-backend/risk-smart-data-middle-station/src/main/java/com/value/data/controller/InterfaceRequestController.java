package com.value.data.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.annotation.Api;
import com.value.data.common.model.LoginUser;
import com.value.data.common.utils.AjaxResult;
import com.value.data.common.utils.SecurityUtils;
import com.value.data.constant.EncryptParamNameConstant;
import com.value.data.constant.IdConstant;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.ApiDTO;
import com.value.data.domain.dto.ApiPageDTO;
import com.value.data.domain.dto.ListInputParameterDTO;
import com.value.data.domain.entity.InterfaceFieldIdManage;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.entity.InterfaceManage;
import com.value.data.domain.vo.EncryptUserVO;
import com.value.data.domain.vo.InputParameterVO;
import com.value.data.tool.CustomNameFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 接口请求控制器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestController
@RequestMapping("/interfaceRequest")
@Slf4j(topic = "[InterfaceRequestController]")
public class InterfaceRequestController {
    private ServiceConstant serviceConstant;

    @Autowired
    public InterfaceRequestController(ServiceConstant serviceConstant){
        this.serviceConstant = serviceConstant;
    }

    /**
     * Api 接口数据调用
     * @param apiDTO
     * @param request
     * @return
     */
    @PostMapping("/api")
    @Api("接口调用")
    public AjaxResult api(@Valid @RequestBody ApiDTO apiDTO,HttpServletRequest request){
        log.info("（1）请求进入数据中台，orderId={}",apiDTO.getOrderId());
        //获取账户信息
        EncryptUserVO encryptVO = serviceConstant.redisService.getCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(apiDTO.getOrderId()));

        InterfaceLog interfaceLog = new InterfaceLog();
        interfaceLog.setChargingFlag(0);//默认计费
        // 获取当前精确日期时间
        LocalDateTime startDateTime = LocalDateTime.now();
        try {
            //唯一标识
            String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
            //日志记录
            interfaceLog.setInterfaceLog(no);
            interfaceLog.setOrderId(apiDTO.getOrderId());
            interfaceLog.setAccessTime(startDateTime);
            interfaceLog.setInterfaceManageNo(apiDTO.getManageNo());
            interfaceLog.setInterfaceName(apiDTO.getInterfaceNo());
            if (encryptVO != null){
                interfaceLog.setUserId(encryptVO.getUserId());
                interfaceLog.setUserDept(encryptVO.getDeptName());
                interfaceLog.setIp(getClientIpAddress(request));
                interfaceLog.setRequestHeaders(getAllHeadersAsString(request));
                interfaceLog.setCreateBy(encryptVO.getNickName());
                interfaceLog.setDeptId(encryptVO.getDeptId());
            }
            //效验被移除数据是否存在
            Boolean validSourceNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(apiDTO.getSourceNo());
            if (validSourceNoBool == false){
                //拦截不允许修改
                saveErrorLog(interfaceLog, "供应商信息不存在", null);
                return AjaxResult.error("供应商信息不存在");
            }
            LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                    .eq(InterfaceManage::getInterfaceSourceNo, apiDTO.getSourceNo())
                    .eq(InterfaceManage::getInterfaceManageNo, apiDTO.getManageNo())
                    .eq(InterfaceManage::getInterfaceNo, apiDTO.getInterfaceNo());
            InterfaceManage interfaceManage = serviceConstant.interfaceManageService.getOne(lambdaQuery);

            if (interfaceManage == null){
                saveErrorLog(interfaceLog, "供应商无该接口信息", null);
                return AjaxResult.error("供应商无该接口信息");
            }

            if (interfaceManage.getInterfaceOn().intValue() == 1){
                saveErrorLog(interfaceLog, "接口未启用", null);
                return AjaxResult.error("接口未启用");
            }



            //接口源地址
            String url = interfaceManage.getInterfaceLink();
            HttpRequest httpRequest = new HttpRequest(url);
            httpRequest.timeout(interfaceManage.getTimeout());
            if (interfaceManage.getRequestType().intValue() == 0){
                httpRequest.method(Method.POST);
            }else if (interfaceManage.getRequestType().intValue() == 1){
                httpRequest.method(Method.GET);
            }

            interfaceLog.setRequestMethod(httpRequest.getMethod().name());

            LambdaQueryWrapper<InterfaceFieldIdManage> fieIdLambdaQuery = Wrappers.lambdaQuery();
            fieIdLambdaQuery.eq(InterfaceFieldIdManage::getDataStatus, 0)
                    .eq(InterfaceFieldIdManage::getInterfaceManageNo, apiDTO.getManageNo());
            List<InterfaceFieldIdManage> fieIdManageList = serviceConstant.interfaceFieldIdManageService.list(fieIdLambdaQuery);


            Map<Integer, List<InterfaceFieldIdManage>> interfaceFieldIdManageByFieldIdType = null;
            if (CollectionUtil.isNotEmpty(fieIdManageList)){
                interfaceFieldIdManageByFieldIdType = fieIdManageList.stream().collect(Collectors.groupingBy(InterfaceFieldIdManage::getInterfaceFieldIdType));
            }else {
                saveErrorLog(interfaceLog, "参数配置异常", null);
                return AjaxResult.error("参数配置异常");
            }

//            apiDTO.getParamData().fluentPut("orderId", apiDTO.getOrderId());

            if (apiDTO.getParamData() == null && interfaceFieldIdManageByFieldIdType.get(0) != null){
                saveErrorLog(interfaceLog, "请求入参异常", null);
                return AjaxResult.error("请求入参异常");
            }else if (apiDTO.getParamData() != null && interfaceFieldIdManageByFieldIdType.get(0) == null){
                saveErrorLog(interfaceLog, "入参配置异常", null);
                return AjaxResult.error("入参配置异常");
            }

            AtomicReference<Boolean> bool = new AtomicReference<>(true);

            AtomicReference<Boolean> oderIdBool = new AtomicReference<>(false);
            //效验是否缺少必填入参
            if (interfaceFieldIdManageByFieldIdType.get(0) != null){
                interfaceFieldIdManageByFieldIdType.get(0).forEach(entity ->{

                    if (entity.getInterfaceFieldIdRequired().intValue() == 0){

                        if (entity.getInterfaceFieldIdName().contains(EncryptParamNameConstant.ORDER_ID))
                            apiDTO.getParamData().fluentPut(EncryptParamNameConstant.ORDER_ID, apiDTO.getOrderId());

                        if (apiDTO.getParamData().containsKey(entity.getInterfaceFieldIdName()) == false)
                            bool.set(false);
                    }
                });
            }

            if (bool.get() == false){
                interfaceLog.setParam(apiDTO.getParamData().toJSONString());
                saveErrorLog(interfaceLog, "缺失必填入参项", null);
                return AjaxResult.error("缺失必填入参项");
            }
            JSONObject bodyObject = new JSONObject();
            if (interfaceFieldIdManageByFieldIdType != null){
                List<InterfaceFieldIdManage> FieldIdTypeList = interfaceFieldIdManageByFieldIdType.get(0);
                FieldIdTypeList.forEach(entity ->{
                    bodyObject.fluentPut(entity.getInterfaceFieldIdName(), apiDTO.getParamData().get(entity.getInterfaceFieldIdName()));
                });
            }
            if (interfaceManage.getParamType().intValue() == 0){
                //JSON
                httpRequest.body(JSON.toJSONString(bodyObject));
            }else if (interfaceManage.getParamType().intValue() == 1){
                //From-data
                httpRequest.form(bodyObject);
            }


            //
            interfaceLog.setParam(bodyObject.toJSONString());
            log.info("（2）数据中台准备请求接口，url:{},params:{}",url,JSON.toJSONString(bodyObject));

            // 带重试的请求逻辑
            String res = null;
            int maxRetry = 2;
            for (int i = 0; i < maxRetry; i++) {
                res = httpRequest.execute().body();
                log.info("（3）数据中台成功获取接口响应,第{}次请求,res:{}", i + 1, res);

                // 检查是否需要重试
                if (JSONUtil.isJsonObj(res)) {
                    JSONObject tempRes = JSONObject.parseObject(res);
                    String msg = tempRes.getString("msg");
                    int code = tempRes.getIntValue("code");
                    // 如果是RPA授权相关错误且不是最后一次重试，等待后重试
                    if (code != 0 && code != 200 && msg != null && msg.contains("授权") && i < maxRetry - 1) {
                        log.warn("外部接口返回授权相关错误[{}]，500ms后进行第{}次重试...", msg, i + 2);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }
                break;
            }

            if (!JSONUtil.isJsonObj(res)){
                saveErrorLog(interfaceLog, "接口访问异常", res);
                return AjaxResult.error("接口访问异常");
            }


            //日志记录
            interfaceLog.setCode(200);
            Object response2 = new Object();


            if (interfaceFieldIdManageByFieldIdType.get(1) != null){
                //指定过滤字段
                SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                interfaceFieldIdManageByFieldIdType.get(1).forEach(entity ->{
                    filter.getIncludes().add(entity.getInterfaceFieldIdName());
                });

                JSONObject resObject = JSONObject.parseObject(res);
                if (!(resObject.getIntValue("code") == 0 || resObject.getIntValue("code") == 200)){
                    return AjaxResult.error(resObject.getString("msg")==null?resObject.getString("message"):resObject.getString("msg"));
                }
                if(resObject.get("chargingFlag")!=null){
                    interfaceLog.setChargingFlag(resObject.getIntValue("chargingFlag"));
                }else {
                    interfaceLog.setChargingFlag(1);
                }

                Object response = new Object();

                String dataStr = resObject.getString("data");
                if (dataStr != null && dataStr.trim().startsWith("{")) {
                    response = JSONObject.parseObject(JSON.toJSONString(resObject.get("data"), filter, JSONWriter.Feature.WriteMapNullValue));
                } else if (dataStr != null && dataStr.trim().startsWith("[")) {
                    response = JSON.parseArray(JSON.toJSONString(resObject.get("data"), filter, JSONWriter.Feature.WriteMapNullValue));
                } else {
                    saveErrorLog(interfaceLog, "数据集非数组、对象", res);
                    return AjaxResult.error("数据集非数组、对象");
                }

                Map<String,String> fieldMapping = new HashMap<>();

                interfaceFieldIdManageByFieldIdType.get(1).forEach(entity ->{
                    fieldMapping.put(entity.getInterfaceFieldIdName(),entity.getInterfaceFieldIdAlias());
                });
                //需要存储完整的结果示例
                String jsonStr = JSON.toJSONString(response, new CustomNameFilter(fieldMapping), JSONWriter.Feature.WriteMapNullValue);
                interfaceLog.setResult(jsonStr);
                LocalDateTime endDateTime = LocalDateTime.now();
                interfaceLog.setResultTime(endDateTime);
                Long millis = Duration.between(startDateTime, endDateTime).toMillis();
                double second = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.UP).doubleValue();
                interfaceLog.setTotalTime(second);
                serviceConstant.interfaceLogService.save(interfaceLog);
                if (jsonStr.trim().startsWith("{")) {
                    return AjaxResult.success(JSONObject.parseObject(jsonStr));
                } else {
                    return AjaxResult.success(JSON.parseArray(jsonStr));
                }
            }else {//没有出参正常返回结果
            	JSONObject resObject = JSONObject.parseObject(res);
                // 校验外部接口返回的code
                if (!(resObject.getIntValue("code") == 0 || resObject.getIntValue("code") == 200)){
                    return AjaxResult.error(resObject.getString("msg")==null?resObject.getString("message"):resObject.getString("msg"));
                }
                if(resObject.get("chargingFlag")!=null){
                    interfaceLog.setChargingFlag(resObject.getIntValue("chargingFlag"));
                }else {
                    interfaceLog.setChargingFlag(1);
                }

                // 处理data为null的情况
                String dataStr2 = resObject.getString("data");
                if (dataStr2 == null) {
                    response2 = null;
                } else if (dataStr2.trim().startsWith("{")) {
                	response2 = JSONObject.parseObject(dataStr2);
                } else if (dataStr2.trim().startsWith("[")) {
                	response2 = JSON.parseArray(dataStr2);
                }
                interfaceLog.setResult(resObject.getString("data"));
            }
            
            LocalDateTime endDateTime = LocalDateTime.now();
            interfaceLog.setResultTime(endDateTime);
            Long millis = Duration.between(startDateTime, endDateTime).toMillis();
            double second = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.UP).doubleValue();
            interfaceLog.setTotalTime(second);
            serviceConstant.interfaceLogService.save(interfaceLog);
            return AjaxResult.success(response2);
        }catch (Exception e){
            saveErrorLog(interfaceLog, e.getMessage(), e.toString());
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }

    /**
     * api 调试页面
     * @param apiDTO
     * @param request
     * @return
     */
    @PostMapping("/apiPage")
    public AjaxResult ApiPageDTO(@Valid @RequestBody ApiPageDTO apiDTO, HttpServletRequest request){
        log.info("（1）请求进入数据中台，orderId={}",apiDTO.getOrderId());
        //获取账户信息
        LoginUser loginUser = SecurityUtils.getLoginUser(request);
//        EncryptUserVO encryptVO = serviceConstant.redisService.getCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(apiDTO.getOrderId()));
        InterfaceLog interfaceLog = new InterfaceLog();
        interfaceLog.setChargingFlag(0);//默认不计费
        // 获取当前精确日期时间
        LocalDateTime startDateTime = LocalDateTime.now();
        try {
            EncryptUserVO encryptVO = new EncryptUserVO();
            encryptVO.setUserName(loginUser.getSysUser().getUserName());
            encryptVO.setDeptName(loginUser.getSysUser().getDept().getDeptName());
            encryptVO.setUserId(loginUser.getSysUser().getUserId());
            encryptVO.setNickName(loginUser.getSysUser().getNickName());
            encryptVO.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            serviceConstant.redisService.setCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(apiDTO.getOrderId()), encryptVO, 30l, TimeUnit.MINUTES);
            //唯一标识
            String no = IdConstant.DATA_MIDDLE_STATION_PREFIX.concat(String.valueOf(serviceConstant.idGeneratorService.generateUniqueId()));
            //日志记录
            interfaceLog.setInterfaceLog(no);
            interfaceLog.setOrderId(apiDTO.getOrderId());
            interfaceLog.setAccessTime(startDateTime);
            interfaceLog.setInterfaceManageNo(apiDTO.getManageNo());
            interfaceLog.setInterfaceName(apiDTO.getInterfaceNo());
            if (loginUser != null){
                interfaceLog.setUserId(loginUser.getSysUser().getUserId());
                interfaceLog.setUserDept(loginUser.getSysUser().getDept().getDeptName());
                interfaceLog.setIp(getClientIpAddress(request));
                interfaceLog.setRequestHeaders(getAllHeadersAsString(request));
                interfaceLog.setCreateBy(loginUser.getSysUser().getNickName());
                interfaceLog.setDeptId(loginUser.getSysUser().getDeptId().intValue());
            }
            //效验被移除数据是否存在
            Boolean validSourceNoBool = serviceConstant.interfaceSourceManageService.validSourceNo(apiDTO.getSourceNo());
            if (validSourceNoBool == false){
                //拦截不允许修改
                saveErrorLog(interfaceLog, "供应商信息不存在", null);
                return AjaxResult.error("供应商信息不存在");
            }
            LambdaQueryWrapper<InterfaceManage> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(InterfaceManage::getDataStatus, 0)
                    .eq(InterfaceManage::getInterfaceSourceNo, apiDTO.getSourceNo())
                    .eq(InterfaceManage::getInterfaceManageNo, apiDTO.getManageNo())
                    .eq(InterfaceManage::getInterfaceNo, apiDTO.getInterfaceNo());
            InterfaceManage interfaceManage = serviceConstant.interfaceManageService.getOne(lambdaQuery);

            if (interfaceManage == null){
                saveErrorLog(interfaceLog, "供应商无该接口信息", null);
                return AjaxResult.error("供应商无该接口信息");
            }

            if (interfaceManage.getInterfaceOn().intValue() == 1){
                saveErrorLog(interfaceLog, "接口未启用", null);
                return AjaxResult.error("接口未启用");
            }



            //接口源地址
            String url = interfaceManage.getInterfaceLink();
            HttpRequest httpRequest = new HttpRequest(url);
            httpRequest.timeout(interfaceManage.getTimeout());
            if (interfaceManage.getRequestType().intValue() == 0){
                httpRequest.method(Method.POST);
            }else if (interfaceManage.getRequestType().intValue() == 1){
                httpRequest.method(Method.GET);
            }

            interfaceLog.setRequestMethod(httpRequest.getMethod().name());

            LambdaQueryWrapper<InterfaceFieldIdManage> fieIdLambdaQuery = Wrappers.lambdaQuery();
            fieIdLambdaQuery.eq(InterfaceFieldIdManage::getDataStatus, 0)
                    .eq(InterfaceFieldIdManage::getInterfaceManageNo, apiDTO.getManageNo());
            List<InterfaceFieldIdManage> fieIdManageList = serviceConstant.interfaceFieldIdManageService.list(fieIdLambdaQuery);


            Map<Integer, List<InterfaceFieldIdManage>> interfaceFieldIdManageByFieldIdType = null;
            if (CollectionUtil.isNotEmpty(fieIdManageList)){
                interfaceFieldIdManageByFieldIdType = fieIdManageList.stream().collect(Collectors.groupingBy(InterfaceFieldIdManage::getInterfaceFieldIdType));
            }else {
                saveErrorLog(interfaceLog, "参数配置异常", null);
                return AjaxResult.error("参数配置异常");
            }

//            apiDTO.getParamData().fluentPut("orderId", apiDTO.getOrderId());

            if (apiDTO.getParamData() == null && interfaceFieldIdManageByFieldIdType.get(0) != null){
                saveErrorLog(interfaceLog, "请求入参异常", null);
                return AjaxResult.error("请求入参异常");
            }else if (apiDTO.getParamData() != null && interfaceFieldIdManageByFieldIdType.get(0) == null){
                saveErrorLog(interfaceLog, "入参配置异常", null);
                return AjaxResult.error("入参配置异常");
            }

            AtomicReference<Boolean> bool = new AtomicReference<>(true);

            AtomicReference<Boolean> oderIdBool = new AtomicReference<>(false);
            //效验是否缺少必填入参
            if (interfaceFieldIdManageByFieldIdType.get(0) != null){
                interfaceFieldIdManageByFieldIdType.get(0).forEach(entity ->{

                    if (entity.getInterfaceFieldIdRequired().intValue() == 0){

                        if (entity.getInterfaceFieldIdName().contains(EncryptParamNameConstant.ORDER_ID))
                            apiDTO.getParamData().fluentPut(EncryptParamNameConstant.ORDER_ID, apiDTO.getOrderId());

                        if (apiDTO.getParamData().containsKey(entity.getInterfaceFieldIdName()) == false)
                            bool.set(false);
                    }
                });
            }

            if (bool.get() == false){
                interfaceLog.setParam(apiDTO.getParamData().toJSONString());
                saveErrorLog(interfaceLog, "缺失必填入参项", null);
                return AjaxResult.error("缺失必填入参项");
            }
            JSONObject bodyObject = new JSONObject();
            if (interfaceFieldIdManageByFieldIdType != null){
                List<InterfaceFieldIdManage> FieldIdTypeList = interfaceFieldIdManageByFieldIdType.get(0);
                FieldIdTypeList.forEach(entity ->{
                    bodyObject.fluentPut(entity.getInterfaceFieldIdName(), apiDTO.getParamData().get(entity.getInterfaceFieldIdName()));
                });
            }
            if (interfaceManage.getParamType().intValue() == 0){
                //JSON
                httpRequest.body(JSON.toJSONString(bodyObject));
            }else if (interfaceManage.getParamType().intValue() == 1){
                //From-data
                httpRequest.form(bodyObject);
            }

            //
            interfaceLog.setParam(bodyObject.toJSONString());

            log.info("（2）数据中台准备请求接口，url:{},params:{}",url,JSON.toJSONString(bodyObject));

            // 带重试的请求逻辑
            String res = null;
            int maxRetry = 2;
            for (int i = 0; i < maxRetry; i++) {
                res = httpRequest.execute().body();
                log.info("（3）数据中台成功获取接口响应,第{}次请求,res:{}", i + 1, res);

                // 检查是否需要重试
                if (JSONUtil.isJsonObj(res)) {
                    JSONObject tempRes = JSONObject.parseObject(res);
                    String msg = tempRes.getString("msg");
                    int code = tempRes.getIntValue("code");
                    // 如果是RPA授权相关错误且不是最后一次重试，等待后重试
                    if (code != 0 && code != 200 && msg != null && msg.contains("授权") && i < maxRetry - 1) {
                        log.warn("外部接口返回授权相关错误[{}]，500ms后进行第{}次重试...", msg, i + 2);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }
                }
                break;
            }

            if (!JSONUtil.isJsonObj(res)){
                saveErrorLog(interfaceLog, "接口访问异常", res);
                return AjaxResult.error("接口访问异常");
            }


            //日志记录
            interfaceLog.setCode(200);
//            Object response2 = new Object();
            Object response2 = new HashMap<String,Object>();//解决Jackson无法序列化空Object的问题


            if (interfaceFieldIdManageByFieldIdType.get(1) != null){
                //指定过滤字段
                SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                interfaceFieldIdManageByFieldIdType.get(1).forEach(entity ->{
                    filter.getIncludes().add(entity.getInterfaceFieldIdName());
                });

                JSONObject resObject = JSONObject.parseObject(res);
                if (!(resObject.getIntValue("code") == 0 || resObject.getIntValue("code") == 200)){
                    return AjaxResult.error(resObject.getString("msg")==null?resObject.getString("message"):resObject.getString("msg"));
                }
                //felix 2023 11 30号增加逻辑
                if(resObject.get("chargingFlag")!=null){
                    interfaceLog.setChargingFlag(resObject.getIntValue("chargingFlag"));
                }else {
                    interfaceLog.setChargingFlag(1);
                }
                Object response = new Object();

                String dataStr = resObject.getString("data");
                if (dataStr != null && dataStr.trim().startsWith("{")) {
                    response = JSONObject.parseObject(JSON.toJSONString(resObject.get("data"), filter, JSONWriter.Feature.WriteMapNullValue));
                } else if (dataStr != null && dataStr.trim().startsWith("[")) {
                    response = JSON.parseArray(JSON.toJSONString(resObject.get("data"), filter, JSONWriter.Feature.WriteMapNullValue));
                } else {
                    saveErrorLog(interfaceLog, "数据集非数组、对象", res);
                    return AjaxResult.error("数据集非数组、对象");
                }

                Map<String,String> fieldMapping = new HashMap<>();

                interfaceFieldIdManageByFieldIdType.get(1).forEach(entity ->{
                    fieldMapping.put(entity.getInterfaceFieldIdName(),entity.getInterfaceFieldIdAlias());
                });
                String jsonStr = JSON.toJSONString(response, new CustomNameFilter(fieldMapping), JSONWriter.Feature.WriteMapNullValue);
                interfaceLog.setResult(jsonStr);
                LocalDateTime endDateTime = LocalDateTime.now();
                interfaceLog.setResultTime(endDateTime);
                Long millis = Duration.between(startDateTime, endDateTime).toMillis();
                double second = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.UP).doubleValue();
                interfaceLog.setTotalTime(second);
                serviceConstant.interfaceLogService.save(interfaceLog);
                if (jsonStr.trim().startsWith("{")) {
                    return AjaxResult.success(JSONObject.parseObject(jsonStr));
                } else {
                    return AjaxResult.success(JSON.parseArray(jsonStr));
                }
            }else {//没有出参正常返回结果
            	JSONObject resObject = JSONObject.parseObject(res);
                // 校验外部接口返回的code
                if (!(resObject.getIntValue("code") == 0 || resObject.getIntValue("code") == 200)){
                    return AjaxResult.error(resObject.getString("msg")==null?resObject.getString("message"):resObject.getString("msg"));
                }
                if(resObject.get("chargingFlag")!=null){
                    interfaceLog.setChargingFlag(resObject.getIntValue("chargingFlag"));
                }else {
                    interfaceLog.setChargingFlag(1);
                }

                // 处理data为null的情况
                String dataStr2 = resObject.getString("data");
                if (dataStr2 == null) {
                    response2 = null;
                } else if (dataStr2.trim().startsWith("{")) {
                	response2 = JSONObject.parseObject(dataStr2);
                } else if (dataStr2.trim().startsWith("[")) {
                	response2 = JSON.parseArray(dataStr2);
                }
                interfaceLog.setResult(resObject.getString("data"));
            }

            LocalDateTime endDateTime = LocalDateTime.now();
            interfaceLog.setResultTime(endDateTime);
            Long millis = Duration.between(startDateTime, endDateTime).toMillis();
            double second = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(1000)).setScale(2, RoundingMode.UP).doubleValue();
            interfaceLog.setTotalTime(second);
            serviceConstant.interfaceLogService.save(interfaceLog);
            return AjaxResult.success(response2);
        }catch (Exception e){
            saveErrorLog(interfaceLog, e.getMessage(), e.toString());
            log.error(e.getMessage(), e);
            return AjaxResult.error("业务中断");
        }
    }

    private void saveErrorLog(InterfaceLog interfaceLog, String errorMsg, String error){
        interfaceLog.setCode(500);
        interfaceLog.setErrorMsg(errorMsg);
        interfaceLog.setError(error);
        serviceConstant.interfaceLogService.save(interfaceLog);
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        return ipAddress;
    }

    public String getAllHeadersAsString(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append("\n");
        }

        return headers.toString();
    }

    /**
     * 根据manageNo获取入参列表
     */
    @PostMapping("/api/listInputParameter")
    public AjaxResult listInputParameter(@RequestBody ListInputParameterDTO param){
        LambdaQueryWrapper<InterfaceFieldIdManage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterfaceFieldIdManage::getInterfaceManageNo,param.getManageNo())
                .eq(InterfaceFieldIdManage::getInterfaceFieldIdType,0);
        List<InterfaceFieldIdManage> interfaceFieldIdManages = serviceConstant.interfaceFieldIdManageService.list(wrapper);
        if (interfaceFieldIdManages==null || interfaceFieldIdManages.size()<1)
            return AjaxResult.success(new ArrayList<InputParameterVO>());

        List<InputParameterVO> inputParameterList = interfaceFieldIdManages.stream().distinct().map(i -> {
            InputParameterVO inputParameter = new InputParameterVO();
            BeanUtil.copyProperties(i, inputParameter);
            return inputParameter;
        }).collect(Collectors.toList());

        return AjaxResult.success(inputParameterList);
    }
}
