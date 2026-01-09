package com.value.data.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.value.data.annotation.Api;
import com.risksmart.common.core.exception.ServiceException;
import com.value.data.config.RequestWrapper;
import com.value.data.constant.EncryptParamNameConstant;
import com.value.data.constant.IdConstant;
import com.value.data.constant.ServiceConstant;
import com.value.data.domain.dto.InterfaceUserDTO;
import com.value.data.domain.entity.InterfaceLog;
import com.value.data.domain.entity.InterfacePermissionsManage;
import com.value.data.domain.vo.EncryptUserVO;
import com.value.data.service.InterfaceUserService;
import com.value.data.tool.SignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    private Long expirationTimeInSeconds;
    private ServiceConstant serviceConstant;

    @Autowired
    @Lazy
    private InterfaceUserService interfaceUserService;



    @Autowired
    public ApiInterceptor(ServiceConstant serviceConstant, @Value("${signature.expirationTimeInSeconds:300}") Long expirationTimeInSeconds){
        this.serviceConstant = serviceConstant;
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime =System.currentTimeMillis()/1000;
        // 检查请求方法是否为HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断方法是否标注了@Api注解
            if (handlerMethod.hasMethodAnnotation(Api.class)) {
                // 获取注解信息
                Api sentinelResource = handlerMethod.getMethodAnnotation(Api.class);
                // 获取资源名称，可以根据需要自定义，例如根据请求路径或其他信息
                String resourceName = sentinelResource.value();

                String body = StrUtil.EMPTY;
                if (request instanceof RequestWrapper) {
                    body = ((RequestWrapper) request).getBody();
                }

                if(!JSONUtil.isJsonObj(body)){
                    throw new ServiceException("非法请求体");
                }

                Map<String, String> bodyMap = JSON.parseObject(body, new TypeReference<HashMap<String, String>>(){});

                //订单号效验
                String orderId = bodyMap.get(EncryptParamNameConstant.ORDER_ID);
                String regex = "^.{1,32}$";
                if (StrUtil.isBlank(orderId) || !orderId.matches(regex))
                    throw new ServiceException(EncryptParamNameConstant.ORDER_ID.concat(" 参数异常"));

                EncryptUserVO encryptVO = serviceConstant.redisService.getCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(orderId));
                if (ObjectUtil.isNotNull(encryptVO))
                    return true;

                //效验签名
                String sign = bodyMap.get(EncryptParamNameConstant.SIGN);
                if (StrUtil.isBlank(sign))
                    throw new ServiceException(EncryptParamNameConstant.SIGN.concat(" 参数缺失"));

                //效验标识
                String appkey = bodyMap.get(EncryptParamNameConstant.APP_KEY);
                if (StrUtil.isBlank(appkey))
                    throw new ServiceException(EncryptParamNameConstant.APP_KEY.concat(" 参数缺失"));

                //效验时间戳
                // 定义日期时间格式的正则表达式
                regex = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
                String timestampStr = bodyMap.get(EncryptParamNameConstant.TIMESTAMP);
                if (StrUtil.isBlank(timestampStr) || !timestampStr.matches(regex))
                    throw new ServiceException(EncryptParamNameConstant.TIMESTAMP.concat(" 参数异常"));
                //获取账户信息 验签
                encryptVO = new EncryptUserVO();
                InterfaceUserDTO interfaceUserDTO = interfaceUserService.queryUserInfoByAppKey(appkey);


                BeanUtil.copyProperties(interfaceUserDTO, encryptVO, false);
                String secret = encryptVO.getSecret();

                if (StrUtil.isBlank(secret))
                    throw new ServiceException("无效appkey");


                //构建签名数据源并验证签名（使用 HMAC-SHA256）
                bodyMap.remove(EncryptParamNameConstant.SIGN);
                String signData = SignatureUtil.buildSignData(bodyMap, secret);

                boolean verify = SignatureUtil.verifySignature(signData, secret, sign);
                if (!verify)
                    throw new ServiceException("签名验证失败");

                boolean checkTimeStamp = SignatureUtil.isTimestampValid(startTime, DateUtil.parseDateTime(timestampStr).getTime()/1000, expirationTimeInSeconds.longValue());

                if (!checkTimeStamp)
                    throw new ServiceException("访问时间超出 "+expirationTimeInSeconds+" 秒");

                LambdaQueryWrapper<InterfaceLog> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(InterfaceLog::getOrderId, orderId);
                long count = serviceConstant.interfaceLogService.count(lambdaQuery);
                if (count > 0)
                    throw new ServiceException(EncryptParamNameConstant.ORDER_ID.concat(" 订单号重复"));

                LambdaQueryWrapper<InterfacePermissionsManage> interfacePermissionslambdaQuery = Wrappers.lambdaQuery();
                interfacePermissionslambdaQuery.eq(InterfacePermissionsManage::getInterfaceManageNo, bodyMap.get("manageNo"))
                        .eq(InterfacePermissionsManage::getUserId, encryptVO.getUserId());

                long permissionsCount = serviceConstant.interfacePermissionsManageService.count(interfacePermissionslambdaQuery);
                if (permissionsCount <= 0)
                    throw new ServiceException("无接口访问权限");

                serviceConstant.redisService.setCacheObject(IdConstant.USER_ORDER_ID_REDIS.concat(orderId), encryptVO, 30l, TimeUnit.MINUTES);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 检查请求方法是否为HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断方法是否标注了@SentinelResource注解
            if (handlerMethod.hasMethodAnnotation(Api.class)) {
                // 获取注解信息
                Api sentinelResource = handlerMethod.getMethodAnnotation(Api.class);
                // 获取资源名称，可以根据需要自定义，例如根据请求路径或其他信息
                String resourceName = sentinelResource.value();
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 检查请求方法是否为HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 判断方法是否标注了@SentinelResource注解
            if (handlerMethod.hasMethodAnnotation(Api.class)) {
                // 获取注解信息
                Api sentinelResource = handlerMethod.getMethodAnnotation(Api.class);
                // 获取资源名称，可以根据需要自定义，例如根据请求路径或其他信息
                String resourceName = sentinelResource.value();
            }
        }
    }


}
